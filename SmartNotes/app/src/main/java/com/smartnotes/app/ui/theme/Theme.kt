package com.smartnotes.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = InkBlue,
    onPrimary = Color.White,
    secondary = AccentAmber,
    onSecondary = Color(0xFF2B2110),
    background = PaperCream,
    onBackground = Color(0xFF1C1B16),
    surface = SurfaceCard,
    onSurface = Color(0xFF1C1B16),
    surfaceVariant = Color(0xFFE8E0D2),
    onSurfaceVariant = Color(0xFF4A453C)
)

private val DarkColors = darkColorScheme(
    primary = InkBlueDark,
    onPrimary = Color(0xFF0C2438),
    secondary = AccentAmber,
    onSecondary = Color(0xFF2B2110),
    background = PaperNight,
    onBackground = Color(0xFFE8E6E1),
    surface = SurfaceCardNight,
    onSurface = Color(0xFFE8E6E1),
    surfaceVariant = Color(0xFF2A313C),
    onSurfaceVariant = Color(0xFFC5C0B6)
)

@Composable
fun SmartNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
