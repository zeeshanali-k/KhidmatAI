package com.corestack.khidmatai.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.ui.theme.*
import khidmatai.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun MockPushNotification(
    visible: Boolean,
    onTap: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTap() },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = MaterialTheme.spacing.mediumSmall,
                bottomEnd = MaterialTheme.spacing.mediumSmall
            ),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.spacing.mediumSmall)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.mediumSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall)
            ) {
                // App icon
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.spacing.extraLarge)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("K", color = Surface, style = AppTypography.labelMedium)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(Res.string.notif_app_name),
                        style = AppTypography.labelMedium,
                        color = TextPrimary
                    )
                    Text(
                        stringResource(Res.string.notif_message),
                        style = AppTypography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2
                    )
                }

                Text(
                    stringResource(Res.string.notif_time),
                    style = AppTypography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
