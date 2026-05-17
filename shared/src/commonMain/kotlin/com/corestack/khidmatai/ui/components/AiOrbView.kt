package com.corestack.khidmatai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import com.corestack.khidmatai.domain.model.AiOrbState
import com.corestack.khidmatai.ui.theme.*

@Composable
fun AiOrbView(
    state: AiOrbState,
    size: Dp
) {
    val transition = rememberInfiniteTransition()

    val scale by transition.animateFloat(
        initialValue = if (state == AiOrbState.IDLE) 0.95f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (state == AiOrbState.THINKING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val backgroundBrush = when (state) {
        AiOrbState.THINKING -> Brush.sweepGradient(listOf(Primary, PrimaryDark, Primary))
        AiOrbState.DONE -> Brush.radialGradient(listOf(SuccessLight, Success))
        AiOrbState.ERROR -> Brush.radialGradient(listOf(ErrorLight, Error))
        AiOrbState.IDLE -> Brush.radialGradient(listOf(PrimaryLight, Primary))
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (state == AiOrbState.IDLE) scale else 1f)
            .rotate(rotation)
            .background(brush = backgroundBrush, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (state == AiOrbState.DONE) {
            Text("✅", color = Surface) // Simple check icon
        }
    }
}
