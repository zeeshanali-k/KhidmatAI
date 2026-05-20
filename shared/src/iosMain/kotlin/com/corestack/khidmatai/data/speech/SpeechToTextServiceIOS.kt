@file:OptIn(ExperimentalForeignApi::class)

package com.corestack.khidmatai.data.speech

import com.corestack.khidmatai.domain.speech.SpeechState
import com.corestack.khidmatai.domain.speech.SpeechToTextService
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.Speech.*
import platform.darwin.NSObject

@Single(binds = [SpeechToTextService::class])
class SpeechToTextServiceIOS : SpeechToTextService {

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    override val state: StateFlow<SpeechState> = _state.asStateFlow()

    private val _rmsdB = MutableStateFlow(0f)
    override val rmsdB: StateFlow<Float> = _rmsdB.asStateFlow()

    private var audioEngine: AVAudioEngine? = null
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null

    override val isSupported: Boolean
        get() {
            val recognizer = SFSpeechRecognizer()
            return recognizer != null && recognizer.isAvailable()
        }

    override fun startListening() {
        _state.value = SpeechState.Listening
        CoroutineScope(Dispatchers.Main).launch {
            try {
                SFSpeechRecognizer.requestAuthorization { status ->
                    CoroutineScope(Dispatchers.Main).launch {
                        when (status) {
                            SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized -> {
                                startSpeechRecognition()
                            }
                            SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusDenied -> {
                                _state.value = SpeechState.Error("Microphone or speech permission denied")
                            }
                            SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusRestricted -> {
                                _state.value = SpeechState.Error("Speech recognition is restricted on this device")
                            }
                            SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusNotDetermined -> {
                                _state.value = SpeechState.Error("Speech permission not determined")
                            }
                            else -> {
                                _state.value = SpeechState.Error("Unknown speech authorization status")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = SpeechState.Error(e.message ?: "Failed to authorize speech recognition")
            }
        }
    }

    private fun startSpeechRecognition() {
        try {
            cleanup()

            val recognizer = SFSpeechRecognizer()
            if (recognizer == null || !recognizer.isAvailable()) {
                _state.value = SpeechState.Error("Speech recognizer is unavailable")
                return
            }

            audioEngine = AVAudioEngine()
            recognitionRequest = SFSpeechAudioBufferRecognitionRequest().apply {
                // Strict local offline preference if supported, otherwise allow online fallback for testing
                requiresOnDeviceRecognition = recognizer.supportsOnDeviceRecognition()
                shouldReportPartialResults = true
            }

            val inputNode = audioEngine?.inputNode
            if (inputNode == null) {
                _state.value = SpeechState.Error("Audio input node is unavailable")
                return
            }

            // Configure audio session for recording
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(
                category = AVAudioSessionCategoryPlayAndRecord,
                withOptions = AVAudioSessionCategoryOptionDefaultToSpeaker,
                error = null
            )
            audioSession.setActive(active = true, error = null)

            val recordingFormat = inputNode.outputFormatForBus(0u)
            
            // Install audio tap to feed the recognizer and calculate RMS
            inputNode.installTapOnBus(
                bus = 0u,
                bufferSize = 1024u,
                format = recordingFormat
            ) { buffer: AVAudioPCMBuffer?, _ ->
                if (buffer != null) {
                    recognitionRequest?.appendAudioPCMBuffer(buffer)
                    
                    // Compute amplitude/RMS for Compose visualizer
                    val frameLength = buffer.frameLength.toInt()
                    val floatChannelDataPointer = buffer.floatChannelData
                    val channelData = floatChannelDataPointer?.get(0)
                    if (channelData != null && frameLength > 0) {
                        var sum = 0f
                        for (i in 0 until frameLength) {
                            val sample = channelData[i.toLong()]
                            sum += sample * sample
                        }
                        val rms = kotlin.math.sqrt(sum / frameLength)
                        val db = if (rms > 0f) 20f * kotlin.math.log10(rms) else -160f
                        // Map standard audio DB range (-60dB to 0dB) to a visually pleasing visualizer amplitude
                        val normalizedAmplitude = ((db + 60f) / 6f).coerceIn(0f, 10f)
                        _rmsdB.value = normalizedAmplitude
                    }
                }
            }

            audioEngine?.prepare()
            audioEngine?.startAndReturnError(null)

            recognitionTask = recognizer.recognitionTaskWithRequest(
                request = recognitionRequest!!,
                delegate = object : NSObject(), SFSpeechRecognitionTaskDelegateProtocol {
                    override fun speechRecognitionTask(
                        task: SFSpeechRecognitionTask,
                        didHypothesizeTranscription: SFTranscription
                    ) {
                        val text = didHypothesizeTranscription.formattedString
                        _state.value = SpeechState.Result(text, isFinal = false)
                    }

                    override fun speechRecognitionTask(
                        task: SFSpeechRecognitionTask,
                        didFinishRecognition: SFSpeechRecognitionResult
                    ) {
                        val text = didFinishRecognition.bestTranscription.formattedString
                        _state.value = SpeechState.Result(text, isFinal = true)
                    }

                    override fun speechRecognitionTask(
                        task: SFSpeechRecognitionTask,
                        didFinishSuccessfully: Boolean
                    ) {
                        if (!didFinishSuccessfully) {
                            if (_state.value is SpeechState.Listening) {
                                _state.value = SpeechState.Error("Speech recognition failed")
                            }
                        }
                    }
                }
            )

            _state.value = SpeechState.Listening

        } catch (e: Exception) {
            _state.value = SpeechState.Error(e.message ?: "Failed to start speech recognition")
            cleanup()
        }
    }

    override fun stopListening() {
        CoroutineScope(Dispatchers.Main).launch {
            audioEngine?.stop()
            recognitionRequest?.endAudio()
        }
    }

    override fun cancel() {
        CoroutineScope(Dispatchers.Main).launch {
            cleanup()
            _state.value = SpeechState.Idle
            _rmsdB.value = 0f
        }
    }

    private fun cleanup() {
        audioEngine?.stop()
        audioEngine?.inputNode?.removeTapOnBus(0u)
        audioEngine = null
        recognitionRequest?.endAudio()
        recognitionRequest = null
        recognitionTask?.cancel()
        recognitionTask = null
    }
}
