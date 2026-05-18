package com.corestack.khidmatai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.corestack.khidmatai.ui.theme.*

enum class BadgeVariant {
    COMPLETED, PENDING, FAILED, UPCOMING, EMERGENCY
}

@Composable
fun StatusBadge(
    variant: BadgeVariant,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (variant) {
        BadgeVariant.COMPLETED -> Triple(Success, Surface, "Mukammal")
        BadgeVariant.PENDING -> Triple(Warning, Surface, "Jari hai")
        BadgeVariant.FAILED -> Triple(Error, Surface, "Nakam")
        BadgeVariant.UPCOMING -> Triple(Primary, Surface, "Aane wala")
        BadgeVariant.EMERGENCY -> Triple(Error, Surface, "Emergency")
    }

    Text(
        text = text,
        color = textColor,
        style = AppTypography.labelMedium,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(MaterialTheme.spacing.xxl))
            .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall)
    )
}
