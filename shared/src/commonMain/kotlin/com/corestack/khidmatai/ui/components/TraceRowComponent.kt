package com.corestack.khidmatai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.TraceItem
import com.corestack.khidmatai.ui.theme.*

@Composable
fun TraceRowComponent(
    item: TraceItem,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        crossAxisAlignment = Alignment.Top
    ) {
        // Icon / Timeline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            StatusIcon(status = item.status)
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(Border)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
            Spacer(modifier = Modifier.height(4.dp))
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
    val transition = rememberInfiniteTransition()
    
    when (status) {
        "completed" -> {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Success),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Surface, style = AppTypography.labelSmall)
            }
        }
        "pending" -> {
            val alpha by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = alpha))
            )
        }
        "failed" -> {
            Box(
                modifier = Modifier
                    .size(24.dp)
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
                    .size(24.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .border(2.dp, Border, CircleShape)
            )
        }
    }
}

fun translateStage(stage: String): String {
    return when (stage) {
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
}
