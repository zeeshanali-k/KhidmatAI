package com.corestack.khidmatai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.corestack.khidmatai.domain.model.TraceItem
import com.corestack.khidmatai.ui.theme.*

@Composable
fun TraceRowComponent(
    item: TraceItem,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.Top
    ) {
        // Icon / Timeline column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(MaterialTheme.spacing.extraLarge)
        ) {
            StatusIcon(status = item.status)
            if (!isLast) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.spacing.extraSmall / 4) // 1dp
                        .height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.extraSmall) // 36dp
                        .background(Border)
                )
            }
        }

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.mediumSmall))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translateStage(item.stage),
                    style = AppTypography.labelMedium,
                    color = TextPrimary
                )
                if (item.status != "waiting") {
                    StatusBadge(
                        variant = when (item.status) {
                            "completed" -> BadgeVariant.COMPLETED
                            "pending" -> BadgeVariant.PENDING
                            "failed" -> BadgeVariant.FAILED
                            else -> BadgeVariant.PENDING
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
            Text(
                text = item.message,
                style = AppTypography.labelSmall, // Mono
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StatusIcon(status: String) {
    when (status) {
        "completed" -> {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .clip(CircleShape)
                    .background(Success),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Surface, style = AppTypography.labelSmall)
            }
        }
        "pending" -> {
            val transition = rememberInfiniteTransition(label = "pending_pulse")
            val alpha by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = alpha))
            )
        }
        "failed" -> {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .clip(CircleShape)
                    .background(Error),
                contentAlignment = Alignment.Center
            ) {
                Text("✕", color = Surface, style = AppTypography.labelSmall)
            }
        }
        else -> { // waiting
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.large)
                    .padding(MaterialTheme.spacing.extraSmall)
                    .clip(CircleShape)
                    .border(MaterialTheme.spacing.extraSmall / 2, Border, CircleShape)
            )
        }
    }
}

fun translateStage(stage: String): String = when (stage) {
    "intent_detection" -> "Apki request samjhi"
    "llm_analysis" -> "AI analysis"
    "service_classification" -> "Service identify ki"
    "urgency_classification" -> "Urgency level set"
    "provider_discovery" -> "Providers dhundhe"
    "provider_ranking" -> "Best match chuna"
    "provider_selection" -> "Provider select kiya"
    "booking_execution" -> "Booking confirm ki"
    "followup" -> "Reminders set kiye"
    else -> stage
}
