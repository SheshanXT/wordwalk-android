package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkForestPrimary,
    onPrimary = DarkForestOnPrimary,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = ForestOnPrimaryContainer,
    secondary = ForestSecondary,
    onSecondary = Color.White,
    secondaryContainer = DarkForestSurfaceVariant,
    onSecondaryContainer = ForestSecondaryContainer,
    tertiary = DarkGoldAccent,
    onTertiary = Color(0xFF382600),
    tertiaryContainer = Color(0xFF533B0B),
    onTertiaryContainer = GoldTertiaryContainer,
    background = DarkForestBackground,
    onBackground = Color(0xFFE6EFE9),
    surface = DarkForestSurface,
    onSurface = Color(0xFFE6EFE9),
    surfaceVariant = DarkForestSurfaceVariant,
    onSurfaceVariant = Color(0xFFBCCBC1),
    outline = Color(0xFF6C8274)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestPrimary,
    onPrimary = ForestOnPrimary,
    primaryContainer = ForestPrimaryContainer,
    onPrimaryContainer = ForestOnPrimaryContainer,
    secondary = ForestSecondary,
    onSecondary = ForestOnSecondary,
    secondaryContainer = ForestSecondaryContainer,
    onSecondaryContainer = ForestOnSecondaryContainer,
    tertiary = GoldAccent,
    onTertiary = Color.White,
    tertiaryContainer = GoldTertiaryContainer,
    onTertiaryContainer = GoldOnTertiaryContainer,
    background = CreamBackground,
    onBackground = CreamOnBackground,
    surface = CreamSurface,
    onSurface = CreamOnSurface,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = CreamOnSurfaceVariant,
    outline = CreamOutline
)

@Composable
fun WordWalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
