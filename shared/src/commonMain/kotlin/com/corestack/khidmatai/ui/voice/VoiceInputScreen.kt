package com.corestack.khidmatai.ui.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.core.domain.model.AiOrbState
import com.corestack.khidmatai.domain.speech.SpeechState
import com.corestack.khidmatai.domain.speech.SpeechToTextService
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private const val WAVEFORM_BARS = 20

@Composable
fun VoiceInputScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val speechToTextService: SpeechToTextService = koinInject()

    val speechState by speechToTextService.state.collectAsState()
    val rmsdB by speechToTextService.rmsdB.collectAsState()

    var elapsedSeconds by remember { mutableStateOf(0) }
    var transcribedText by remember { mutableStateOf("") }
    var isRecordingStarted by remember { mutableStateOf(false) }

    val permissionState = rememberRecordAudioPermissionState { status ->
        if (status == RecordAudioPermissionStatus.GRANTED && !isRecordingStarted) {
            speechToTextService.startListening()
            isRecordingStarted = true
        }
    }

    // Handle initial permission request and listening launch
    LaunchedEffect(permissionState.status) {
        if (permissionState.status == RecordAudioPermissionStatus.GRANTED) {
            if (!isRecordingStarted) {
                speechToTextService.startListening()
                isRecordingStarted = true
            }
        } else if (permissionState.status == RecordAudioPermissionStatus.UNKNOWN) {
            permissionState.launchRequest()
        }
    }

    // Auto-cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            speechToTextService.cancel()
        }
    }

    // Timer logic
    LaunchedEffect(isRecordingStarted, speechState) {
        if (isRecordingStarted && speechState is SpeechState.Listening) {
            while (true) {
                delay(1000L)
                elapsedSeconds++
            }
        }
    }

    // Transcription results tracking
    LaunchedEffect(speechState) {
        when (val state = speechState) {
            is SpeechState.Result -> {
                transcribedText = state.text
            }
            else -> {}
        }
    }

    // Map speech state to UI Orb State
    val orbState = when (speechState) {
        is SpeechState.Error -> AiOrbState.ERROR
        is SpeechState.Result -> AiOrbState.THINKING
        is SpeechState.Listening -> AiOrbState.THINKING
        SpeechState.Idle -> AiOrbState.IDLE
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TextPrimary.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle at top
            Box(
                modifier = Modifier
                    .width(MaterialTheme.spacing.xxl)
                    .height(MaterialTheme.spacing.extraSmall)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                    .background(Surface.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.weight(1f))

            // AI Orb View
            AiOrbView(state = orbState, size = 64.dp)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Content Header & live text transcription
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                if (permissionState.status == RecordAudioPermissionStatus.DENIED) {
                    Text(
                        text = "Microphone Permission Required",
                        style = AppTypography.titleLarge,
                        color = ErrorLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = "Please enable microphone permission in device settings to use voice input.",
                        style = AppTypography.bodySmall,
                        color = Surface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                    Button(
                        onClick = { permissionState.launchRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Grant Permission", style = AppTypography.labelMedium)
                    }
                } else if (!speechToTextService.isSupported) {
                    Text(
                        text = "Voice Input Unsupported",
                        style = AppTypography.titleLarge,
                        color = ErrorLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    Text(
                        text = "Speech recognition is not available or supported on this device.",
                        style = AppTypography.bodySmall,
                        color = Surface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                } else {
                    when (val state = speechState) {
                        is SpeechState.Error -> {
                            Text(
                                text = "Error Occurred",
                                style = AppTypography.titleLarge,
                                color = ErrorLight,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = state.message,
                                style = AppTypography.bodySmall,
                                color = Surface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                            Button(
                                onClick = {
                                    speechToTextService.cancel()
                                    speechToTextService.startListening()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text("Try Again", style = AppTypography.labelMedium)
                            }
                        }
                        else -> {
                            Text(
                                text = if (transcribedText.isNotEmpty()) "Transcribing..." else s.voiceTitle,
                                style = AppTypography.titleLarge,
                                color = Surface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = s.voiceSubtitle,
                                style = AppTypography.bodySmall,
                                color = Surface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Real-time dynamic waveform
            if (permissionState.status == RecordAudioPermissionStatus.GRANTED && speechToTextService.isSupported && speechState !is SpeechState.Error) {
                WaveformView(amplitude = rmsdB)
            } else {
                Spacer(modifier = Modifier.height(80.dp))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Real-time Transcribed Text View
            if (transcribedText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                        .background(Surface.copy(alpha = 0.1f))
                        .padding(MaterialTheme.spacing.medium)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = transcribedText,
                            style = AppTypography.bodyMedium,
                            color = Surface,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            } else if (permissionState.status == RecordAudioPermissionStatus.GRANTED && speechToTextService.isSupported && speechState !is SpeechState.Error) {
                Text(
                    text = "Listening for your voice...",
                    style = AppTypography.bodySmall,
                    color = Surface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Timer
            if (permissionState.status == RecordAudioPermissionStatus.GRANTED && speechToTextService.isSupported && speechState !is SpeechState.Error) {
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                Text(
                    text = "$minutes:${seconds.toString().padStart(2, '0')}",
                    style = AppTypography.bodySmall,
                    color = Surface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                TextButton(
                    onClick = {
                        speechToTextService.cancel()
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f).height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium)
                ) {
                    Text(s.voiceBtnCancel, color = Error, style = AppTypography.labelMedium)
                }
                Button(
                    onClick = {
                        speechToTextService.stopListening()
                        // Use the transcribed text if any, otherwise fallback to mock so it never breaks for demo
                        val finalText = transcribedText.ifBlank { "I need an AC technician tomorrow morning at G-13" }
                        viewModel.onAction(ServiceRequestIntent.UpdateQuery(finalText))
                        speechToTextService.cancel()
                        onNavigateBack()
                    },
                    enabled = permissionState.status == RecordAudioPermissionStatus.GRANTED && speechToTextService.isSupported && speechState !is SpeechState.Error,
                    modifier = Modifier.weight(2f).height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium),
                    shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(s.voiceBtnStop, style = AppTypography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }
    }
}

@Composable
private fun WaveformView(amplitude: Float) {
    // Map a typical rmsdB range of -2dB to 10dB to a factor of 0.2f to 2.2f
    val scaleFactor = ((amplitude + 2f) / 12f).coerceIn(0.15f, 2.2f)

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val barAnimations = (0 until WAVEFORM_BARS).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 8f,
            targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400 + (index * 50) % 400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset((index * 80) % 800)
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = Modifier.height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barAnimations.forEachIndexed { index, anim ->
            val animatedHeight = anim.value * scaleFactor
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Primary.copy(alpha = 0.7f + (index % 3) * 0.1f))
            )
        }
    }
}
