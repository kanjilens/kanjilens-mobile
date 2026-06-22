package com.example.kanjilens.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    secondary = AppSecondary,
    tertiary = AppPrimaryLight,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppWhite,
    onSecondary = AppWhite,
    onBackground = AppTextPrimary,
    onSurface = AppTextPrimary,
    surfaceVariant = Color(0xFFF4F6F9)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryLight,
    secondary = AppSecondary,
    tertiary = AppPrimary,
    background = Color(0xFF071717),
    surface = Color(0xFF102323),
    onPrimary = AppTextPrimary,
    onSecondary = AppWhite,
    onBackground = AppWhite,
    onSurface = AppWhite,
    surfaceVariant = Color(0xFF1E3535)
)

@Composable
fun KanjiLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
