package com.mohamedabdelazeim.islamicapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Colors ────────────────────────────────────────────────────────────────────
val Gold = Color(0xFFFFD700)
val DarkGreen = Color(0xFF1B5E20)
val DeepBlue = Color(0xFF0D1B2A)
val CardGreen = Color(0xFF1B5E20)
val SurfaceBlue = Color(0xFF0F2133)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = DarkGreen,
    onSecondary = Color.White,
    background = DeepBlue,
    onBackground = Color.White,
    surface = SurfaceBlue,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A2940),
    onSurfaceVariant = Color(0xFFCCCCCC),
)

@Composable
fun IslamicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
