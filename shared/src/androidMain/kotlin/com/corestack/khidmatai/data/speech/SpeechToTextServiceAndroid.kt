package com.corestack.khidmatai.data.speech

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.corestack.khidmatai.domain.speech.SpeechState
import com.corestack.khidmatai.domain.speech.SpeechToTextService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.Locale

private const val TAG = "SpeechToTextAndroid"

@Single(binds = [SpeechToTextService::class])
class SpeechToTextServiceAndroid(
    private val context: Context
) : SpeechToTextService {

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    override val state: StateFlow<SpeechState> = _state.asStateFlow()

    private val _rmsdB = MutableStateFlow(0f)
    override val rmsdB: StateFlow<Float> = _rmsdB.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var rmsLogCounter = 0

    override val isSupported: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context).also {
            Log.d(TAG, "isRecognitionAvailable check: $it")
        }

    private fun logDiagnostics() {
        try {
            Log.d(TAG, "--- SPEECH RECOGNITION DIAGNOSTICS ---")
            Log.d(TAG, "Device Manufacturer: ${Build.MANUFACTURER}")
            Log.d(TAG, "Device Model: ${Build.MODEL}")
            Log.d(TAG, "Android SDK Version: ${Build.VERSION.SDK_INT}")
            Log.d(TAG, "System Default Locale: ${Locale.getDefault()}")
            Log.d(TAG, "SpeechRecognizer isRecognitionAvailable: ${SpeechRecognizer.isRecognitionAvailable(context)}")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val onDeviceAvailable = SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
                Log.d(TAG, "SpeechRecognizer isOnDeviceRecognitionAvailable: $onDeviceAvailable")
            } else {
                Log.d(TAG, "SpeechRecognizer isOnDeviceRecognitionAvailable: N/A (API < 31)")
            }

            val services = context.packageManager.queryIntentServices(
                Intent(RecognitionService.SERVICE_INTERFACE), 0
            )
            Log.d(TAG, "Available RecognitionServices count: ${services.size}")
            services.forEachIndexed { index, resolveInfo ->
                val serviceInfo = resolveInfo.serviceInfo
                Log.d(TAG, "  [$index] Service: ${serviceInfo.packageName}/${serviceInfo.name}")
            }
            
            val activities = context.packageManager.queryIntentActivities(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0
            )
            Log.d(TAG, "Available RecognitionActivities count: ${activities.size}")
            activities.forEachIndexed { index, resolveInfo ->
                val activityInfo = resolveInfo.activityInfo
                Log.d(TAG, "  [$index] Activity: ${activityInfo.packageName}/${activityInfo.name}")
            }
            Log.d(TAG, "--------------------------------------")
        } catch (e: Exception) {
            Log.e(TAG, "Error performing speech recognition diagnostics", e)
        }
    }

    private fun logBundle(label: String, bundle: Bundle?) {
        if (bundle == null) {
            Log.d(TAG, "$label: Bundle is null")
            return
        }
        Log.d(TAG, "$label: Bundle keys start ---")
        try {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                Log.d(TAG, "  Key: '$key' -> Value: '$value' (Type: ${value?.javaClass?.name ?: "null"})")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading bundle keys for $label", e)
        }
        Log.d(TAG, "$label: Bundle keys end ---")
    }

    override fun startListening() {
        Log.d(TAG, "startListening() called on thread: ${Thread.currentThread().name}")
        logDiagnostics()
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val onDeviceAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
                } else {
                    false
                }

                if (speechRecognizer == null) {
                    Log.d(TAG, "Initializing SpeechRecognizer instance. onDeviceAvailable = $onDeviceAvailable")
                    
                    speechRecognizer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && onDeviceAvailable) {
                        Log.d(TAG, "Creating SpeechRecognizer via SpeechRecognizer.createOnDeviceSpeechRecognizer")
                        SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
                    } else {
                        Log.d(TAG, "Creating SpeechRecognizer via SpeechRecognizer.createSpeechRecognizer (Standard)")
                        SpeechRecognizer.createSpeechRecognizer(context)
                    }

                    speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            Log.d(TAG, "onReadyForSpeech callback triggered")
                            logBundle("onReadyForSpeech params", params)
                            _state.value = SpeechState.Listening
                        }

                        override fun onBeginningOfSpeech() {
                            Log.d(TAG, "onBeginningOfSpeech callback triggered")
                            _state.value = SpeechState.Listening
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            _rmsdB.value = rmsdB
                            // Throttle logging to once every 40 callbacks to prevent Logcat flooding
                            if (rmsLogCounter++ % 40 == 0) {
                                Log.d(TAG, "onRmsChanged callback: rmsdB = $rmsdB")
                            }
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {
                            Log.d(TAG, "onBufferReceived callback: buffer size = ${buffer?.size ?: 0} bytes")
                        }

                        override fun onEndOfSpeech() {
                            Log.d(TAG, "onEndOfSpeech callback triggered")
                        }

                        override fun onError(error: Int) {
                            Log.e(TAG, "onError callback triggered: error code = $error")
                            val errorMessage = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error (ERROR_AUDIO)"
                                SpeechRecognizer.ERROR_CLIENT -> "Client-side error / Device configuration/locale mismatch (ERROR_CLIENT)"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied for recording (ERROR_INSUFFICIENT_PERMISSIONS)"
                                SpeechRecognizer.ERROR_NETWORK -> "Network error (ERROR_NETWORK)"
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout (ERROR_NETWORK_TIMEOUT)"
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized (ERROR_NO_MATCH)"
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy (ERROR_RECOGNIZER_BUSY)"
                                SpeechRecognizer.ERROR_SERVER -> "Server error (ERROR_SERVER)"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout/silence (ERROR_SPEECH_TIMEOUT)"
                                else -> "Unknown speech recognition error (code $error)"
                            }
                            Log.e(TAG, "SpeechRecognizer Error Mapped: $errorMessage")
                            _state.value = SpeechState.Error(errorMessage)
                        }

                        override fun onResults(results: Bundle?) {
                            Log.d(TAG, "onResults callback triggered")
                            logBundle("onResults bundle", results)
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            Log.d(TAG, "onResults matches: $matches")
                            val text = matches?.firstOrNull() ?: ""
                            Log.d(TAG, "onResults extracted final text: '$text'")
                            _state.value = SpeechState.Result(text, isFinal = true)
                        }

                        override fun onPartialResults(results: Bundle?) {
                            Log.d(TAG, "onPartialResults callback triggered")
                            logBundle("onPartialResults bundle", results)
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            Log.d(TAG, "onPartialResults matches: $matches")
                            val text = matches?.firstOrNull() ?: ""
                            Log.d(TAG, "onPartialResults extracted partial text: '$text'")
                            if (text.isNotEmpty()) {
                                _state.value = SpeechState.Result(text, isFinal = false)
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {
                            Log.d(TAG, "onEvent callback triggered: eventType = $eventType")
                            logBundle("onEvent params", params)
                        }
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    
                    // Force/prefer offline recognition ONLY if the system reports that on-device recognition is supported.
                    // If it is an emulator or device that reports false, we allow online fallback so the developer can actually test the feature
                    // and get recognition results during design iterations.
                    if (onDeviceAvailable) {
                        Log.d(TAG, "On-device recognition is available on this system. Setting EXTRA_PREFER_OFFLINE = true")
                        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                    } else {
                        Log.w(TAG, "On-device recognition is NOT available/supported on this device/emulator. " +
                                "Allowing online recognition fallback so voice tests do not fail on this platform.")
                    }
                }

                Log.d(TAG, "Speech recognition Intent parameters:")
                Log.d(TAG, "  ACTION: ${intent.action}")
                Log.d(TAG, "  EXTRA_LANGUAGE_MODEL: ${intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL)}")
                Log.d(TAG, "  EXTRA_PARTIAL_RESULTS: ${intent.getBooleanExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)}")
                Log.d(TAG, "  EXTRA_PREFER_OFFLINE: ${intent.hasExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE)}")

                Log.d(TAG, "Calling startListening(intent) on SpeechRecognizer")
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Exception caught in startListening", e)
                _state.value = SpeechState.Error(e.message ?: "Failed to start recognition")
            }
        }
    }

    override fun stopListening() {
        Log.d(TAG, "stopListening() called on thread: ${Thread.currentThread().name}")
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "Executing stopListening on SpeechRecognizer")
            speechRecognizer?.stopListening()
        }
    }

    override fun cancel() {
        Log.d(TAG, "cancel() called on thread: ${Thread.currentThread().name}. Tearing down SpeechRecognizer")
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "Executing cancel/destroy on SpeechRecognizer")
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
            _state.value = SpeechState.Idle
            _rmsdB.value = 0f
            rmsLogCounter = 0
            Log.d(TAG, "SpeechRecognizer torn down successfully")
        }
    }
}
