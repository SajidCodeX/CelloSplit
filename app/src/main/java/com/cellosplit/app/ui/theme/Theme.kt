package com.cellosplit.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6B46C1),
    secondary = Color(0xFFA855F7),
    background = Color(0xFFF8F7FF),
    surface = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA855F7),
    secondary = Color(0xFF6B46C1),
    background = Color(0xFF181321),
    surface = Color(0xFF241B33)
)

@Composable
fun CelloSplitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
