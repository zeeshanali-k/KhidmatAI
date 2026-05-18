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
    val s = LocalAppStrings.current
    val bgColor = when (variant) {
        BadgeVariant.COMPLETED -> Success
        BadgeVariant.PENDING -> Warning
        BadgeVariant.FAILED -> Error
        BadgeVariant.UPCOMING -> Primary
        BadgeVariant.EMERGENCY -> Error
    }
    val label = when (variant) {
        BadgeVariant.COMPLETED -> s.badgeCompleted
        BadgeVariant.PENDING -> s.badgePending
        BadgeVariant.FAILED -> s.badgeFailed
        BadgeVariant.UPCOMING -> s.badgeUpcoming
        BadgeVariant.EMERGENCY -> s.badgeEmergency
    }

    Text(
        text = label,
        color = Surface,
        style = AppTypography.labelMedium,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(MaterialTheme.spacing.xxl))
            .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall)
    )
}
