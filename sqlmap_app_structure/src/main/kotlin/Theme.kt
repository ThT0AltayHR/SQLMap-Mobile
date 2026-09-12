package com.sqlmap.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SQLMapTypography = Typography()
private val SQLMapShapes = Shapes()

// Siber güvenlik teması renkleri
object CyberColors {
    val DarkBackground = Color(0xFF0A0E27)
    val DarkerBackground = Color(0xFF050812)
    val CardBackground = Color(0xFF1A1F3A)
    val BorderColor = Color(0xFF2D3561)
    val PrimaryOrange = Color(0xFFFF6B35)
    val AccentOrange = Color(0xFFFF8C42)
    val SuccessGreen = Color(0xFF4CAF50)
    val WarningYellow = Color(0xFFFFC107)
    val ErrorRed = Color(0xFFFF5252)
    val InfoBlue = Color(0xFF2196F3)
    val TextPrimary = Color(0xFFE0E0E0)
    val TextSecondary = Color(0xFF9E9E9E)
    val TextHint = Color(0xFF616161)
    val GlowOrange = Color(0xFFFF9534)
}

private val DarkColorScheme = darkColorScheme(
    primary = CyberColors.PrimaryOrange,
    secondary = CyberColors.AccentOrange,
    tertiary = CyberColors.InfoBlue,
    background = CyberColors.DarkBackground,
    surface = CyberColors.CardBackground,
    error = CyberColors.ErrorRed,
    onPrimary = CyberColors.DarkerBackground,
    onSecondary = CyberColors.DarkerBackground,
    onBackground = CyberColors.TextPrimary,
    onSurface = CyberColors.TextPrimary,
)

@Composable
fun SQLMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SQLMapTypography,
        shapes = SQLMapShapes,
        content = content
    )
}
