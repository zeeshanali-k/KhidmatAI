package com.corestack.khidmatai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = PrimaryLight,
    secondary = PrimaryDark,
    background = Background,
    surface = Surface,
    onSurface = TextPrimary,
    onBackground = TextPrimary,
    error = Error,
    errorContainer = ErrorLight,
    outline = Border
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
