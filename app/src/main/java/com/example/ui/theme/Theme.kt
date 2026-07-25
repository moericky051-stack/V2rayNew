package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberCyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003847),
    onPrimaryContainer = CyberCyanPrimary,
    secondary = CyberCyanSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0A2E40),
    onSecondaryContainer = CyberCyanSecondary,
    tertiary = CyberEmerald,
    onTertiary = Color.Black,
    background = CyberNavyBg,
    onBackground = CyberTextPrimary,
    surface = CyberCardBg,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardBorder,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCardBorder,
    error = CyberRose,
    onError = Color.White
)

@Composable
fun SecureV2RayTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberDarkColorScheme,
        typography = Typography,
        content = content
    )
}
