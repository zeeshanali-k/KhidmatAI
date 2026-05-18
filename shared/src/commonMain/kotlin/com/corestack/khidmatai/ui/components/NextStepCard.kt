package com.corestack.khidmatai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.corestack.khidmatai.domain.model.NextStep
import com.corestack.khidmatai.ui.theme.*

@Composable
fun NextStepCard(
    step: NextStep,
    onActionClick: (String?) -> Unit
) {
    val isWarning = step.type == "warning"

    Card(
        shape = RoundedCornerShape(MaterialTheme.spacing.mediumSmall),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(MaterialTheme.spacing.extraSmall / 4, Border),
        modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (isWarning) {
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.spacing.extraSmall)
                        .fillMaxHeight()
                        .background(Warning)
                )
            }
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium)
                    .weight(1f)
            ) {
                Row {
                    if (isWarning) {
                        Text("⚠️", modifier = Modifier.padding(end = MaterialTheme.spacing.small))
                    }
                    Text(
                        text = step.title,
                        style = AppTypography.titleLarge,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                Text(
                    text = step.description,
                    style = AppTypography.bodyLarge,
                    color = TextSecondary
                )

                if (step.type == "action" && step.actionLabel != null) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumSmall))
                    Button(
                        onClick = { onActionClick(step.actionValue) },
                        modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(step.actionLabel, color = Surface, style = AppTypography.labelMedium)
                    }
                } else if (step.actionLabel != null) {
                    // Info with action button
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumSmall))
                    OutlinedButton(
                        onClick = { onActionClick(step.actionValue) },
                        modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.mediumSmall),
                        shape = RoundedCornerShape(MaterialTheme.spacing.small),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Text(step.actionLabel, style = AppTypography.labelMedium)
                    }
                }
            }
        }
    }
}
