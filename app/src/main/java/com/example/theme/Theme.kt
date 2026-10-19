package com.example.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = Color.White,
    background = BackgroundWarm,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = TextMuted,
    outline = BorderColor,
    error = DestructiveRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEEEEEE),
    outline = Color(0xFF333333),
    error = DestructiveRed,
    onError = Color.White
)

@Composable
fun DwarSewaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
