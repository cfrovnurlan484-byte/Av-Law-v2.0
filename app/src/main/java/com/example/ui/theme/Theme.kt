package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LegalGold,
    onPrimary = Color(0xFF1E1600),
    primaryContainer = LegalNavyLight,
    onPrimaryContainer = LegalGoldLight,
    secondary = LegalGoldLight,
    onSecondary = Color(0xFF2E2000),
    secondaryContainer = LegalSurfaceVariantDark,
    onSecondaryContainer = LegalTextPrimaryDark,
    background = LegalBackgroundDark,
    onBackground = LegalTextPrimaryDark,
    surface = LegalSurfaceDark,
    onSurface = LegalTextPrimaryDark,
    surfaceVariant = LegalSurfaceVariantDark,
    onSurfaceVariant = LegalTextSecondaryDark,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LegalNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2EDF8),
    onPrimaryContainer = LegalNavyDark,
    secondary = LegalGoldDark,
    onSecondary = Color.White,
    secondaryContainer = LegalGoldLight,
    onSecondaryContainer = Color(0xFF332000),
    background = LegalBackgroundLight,
    onBackground = LegalTextPrimaryLight,
    surface = LegalSurfaceLight,
    onSurface = LegalTextPrimaryLight,
    surfaceVariant = LegalSurfaceVariantLight,
    onSurfaceVariant = LegalTextSecondaryLight,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun AzHuquqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to preserve rich bespoke Azerbaijani Legal Gold & Navy identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
