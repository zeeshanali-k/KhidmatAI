package com.corestack.khidmatai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

enum class BadgeVariant {
    COMPLETED, PENDING, FAILED, UPCOMING, EMERGENCY
}

@Composable
fun StatusBadge(
    variant: BadgeVariant,
    modifier: Modifier = Modifier
) {
    val bgColor = when (variant) {
        BadgeVariant.COMPLETED -> Success
        BadgeVariant.PENDING -> Warning
        BadgeVariant.FAILED -> Error
        BadgeVariant.UPCOMING -> Primary
        BadgeVariant.EMERGENCY -> Error
    }

    val textRes = when (variant) {
        BadgeVariant.COMPLETED -> Res.string.badge_completed
        BadgeVariant.PENDING -> Res.string.badge_pending
        BadgeVariant.FAILED -> Res.string.badge_failed
        BadgeVariant.UPCOMING -> Res.string.badge_upcoming
        BadgeVariant.EMERGENCY -> Res.string.badge_emergency
    }

    Text(
        text = stringResource(textRes),
        color = Surface,
        style = AppTypography.labelMedium,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(MaterialTheme.spacing.xxl))
            .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall)
    )
}
