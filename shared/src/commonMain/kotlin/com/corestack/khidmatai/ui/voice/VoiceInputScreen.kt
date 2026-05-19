package com.corestack.khidmatai.ui.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.core.domain.model.AiOrbState
import com.corestack.khidmatai.ui.components.AiOrbView
import com.corestack.khidmatai.ui.home.ServiceRequestIntent
import com.corestack.khidmatai.ui.home.ServiceRequestViewModel
import com.corestack.khidmatai.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private const val MOCK_TRANSCRIPTION = "I need an AC technician tomorrow morning at G-13"
private const val WAVEFORM_BARS = 20

@Composable
fun VoiceInputScreen(
    viewModel: ServiceRequestViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val s = LocalAppStrings.current
    var elapsedSeconds by remember { mutableStateOf(0) }
    var isTranscribing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
        }
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
            Box(
                modifier = Modifier
                    .width(MaterialTheme.spacing.xxl)
                    .height(MaterialTheme.spacing.extraSmall)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.xxl))
                    .background(Surface.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.weight(1f))

            AiOrbView(state = AiOrbState.THINKING, size = 64.dp)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            if (isTranscribing) {
                Text(s.voiceTranscribing, style = AppTypography.titleLarge, color = Surface)
            } else {
                Text(s.voiceTitle, style = AppTypography.titleLarge, color = Surface)
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(s.voiceSubtitle, style = AppTypography.bodySmall, color = Surface.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            WaveformView()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            Text(
                text = "$minutes:${seconds.toString().padStart(2, '0')}",
                style = AppTypography.bodySmall,
                color = Surface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f).height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium)
                ) {
                    Text(s.voiceBtnCancel, color = Error, style = AppTypography.labelMedium)
                }
                Button(
                    onClick = { isTranscribing = true },
                    enabled = !isTranscribing,
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

    LaunchedEffect(isTranscribing) {
        if (isTranscribing) {
            delay(1000L)
            viewModel.onAction(ServiceRequestIntent.UpdateQuery(MOCK_TRANSCRIPTION))
            onNavigateBack()
        }
    }
}

@Composable
private fun WaveformView() {
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
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(anim.value.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Primary.copy(alpha = 0.7f + (index % 3) * 0.1f))
            )
        }
    }
}
