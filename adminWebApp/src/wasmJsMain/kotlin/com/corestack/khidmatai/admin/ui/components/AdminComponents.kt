package com.corestack.khidmatai.admin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val CardBackground = Color(0xFF1E1E1E)
val BorderColor = Color(0xFF2A2A2A)
val AccentGreen = Color(0xFF00E676)
val TextPrimary = Color(0xFFEEEEEE)
val TextSecondary = Color(0xFF888888)
val StatusConfirmed = Color(0xFF00E676)
val StatusPending = Color(0xFFFFC107)
val StatusCancelled = Color(0xFFEF5350)
val StatusCompleted = Color(0xFF42A5F5)

@Composable
fun AdminCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .background(CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(20.dp),
        content = content
    )
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    AdminCard(modifier = modifier) {
        Text(title, color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusChip(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "confirmed" -> Color(0xFF00E6761A) to StatusConfirmed
        "pending" -> Color(0xFFFFC1071A) to StatusPending
        "cancelled" -> Color(0xFFEF53501A) to StatusCancelled
        "completed" -> Color(0xFF42A5F51A) to StatusCompleted
        "in_progress" -> Color(0xFF9C27B01A) to Color(0xFFCE93D8)
        else -> Color(0xFF3A3A3A) to TextSecondary
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, fg.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replace("_", " ").replaceFirstChar { it.uppercase() },
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String? = null, action: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = TextSecondary, fontSize = 13.sp)
        }
        action?.invoke()
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AccentGreen)
    }
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(message, color = StatusCancelled, fontSize = 14.sp)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)) {
                Text("Retry", color = Color.Black)
            }
        }
    }
}

@Composable
fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary, fontSize = 12.sp) },
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = BorderColor,
            cursorColor = AccentGreen,
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground
        ),
        modifier = modifier
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentGreen,
            disabledContainerColor = Color(0xFF2A2A2A)
        ),
        modifier = modifier
    ) {
        Text(text, color = if (enabled) Color.Black else TextSecondary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DangerButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(StatusCancelled.copy(alpha = 0.5f))
        ),
        modifier = modifier
    ) {
        Text(text, color = StatusCancelled)
    }
}
