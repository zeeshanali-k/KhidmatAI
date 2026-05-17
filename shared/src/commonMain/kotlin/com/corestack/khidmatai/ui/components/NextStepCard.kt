package com.corestack.khidmatai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corestack.khidmatai.domain.model.NextStep
import com.corestack.khidmatai.ui.theme.*

@Composable
fun NextStepCard(
    step: NextStep,
    onActionClick: (String?) -> Unit
) {
    val isWarning = step.type == "warning"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (isWarning) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(Warning)
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Row {
                    if (isWarning) {
                        Text("⚠️", modifier = Modifier.padding(end = 8.dp))
                    }
                    Text(
                        text = step.title,
                        style = AppTypography.titleLarge,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.description,
                    style = AppTypography.bodyLarge,
                    color = TextSecondary
                )

                if (step.type == "action" && step.actionLabel != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onActionClick(step.actionValue) },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(step.actionLabel, color = Surface, style = AppTypography.labelMedium)
                    }
                } else if (step.actionLabel != null) {
                    // Info with action button
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onActionClick(step.actionValue) },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Text(step.actionLabel, style = AppTypography.labelMedium)
                    }
                }
            }
        }
    }
}
