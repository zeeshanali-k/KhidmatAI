package com.corestack.khidmatai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------------------
// Spacing — access via MaterialTheme.spacing throughout the app
// ---------------------------------------------------------------------------
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val mediumSmall: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val screenPadding: Dp = 16.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable
    get() = LocalSpacing.current

// ---------------------------------------------------------------------------
// Color scheme
// ---------------------------------------------------------------------------
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

// ---------------------------------------------------------------------------
// App theme
// ---------------------------------------------------------------------------
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = {
            CompositionLocalProvider(LocalSpacing provides Spacing()) {
                content()
            }
        }
    )
}
