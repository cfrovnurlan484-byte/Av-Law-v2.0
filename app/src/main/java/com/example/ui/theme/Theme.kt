package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EliteLuxuryDarkColorScheme = darkColorScheme(
    primary = LuxuryGold,
    onPrimary = Color(0xFF1E1600),
    primaryContainer = Color(0xFF221A05),
    onPrimaryContainer = LuxuryGoldLight,
    secondary = LuxuryGoldLight,
    onSecondary = Color(0xFF2E2000),
    secondaryContainer = Color(0xFF1F2430),
    onSecondaryContainer = LuxuryTextHighContrast,
    tertiary = LuxuryGoldAmber,
    onTertiary = Color(0xFF201300),
    background = MidnightNavyBackground,
    onBackground = LuxuryTextHighContrast,
    surface = MidnightNavySurface,
    onSurface = LuxuryTextHighContrast,
    surfaceVariant = MidnightNavySurfaceElevated,
    onSurfaceVariant = LuxuryTextMuted,
    outline = Color(0xFF3A4454),
    outlineVariant = Color(0x33D4AF37),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun AzHuquqTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EliteLuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
