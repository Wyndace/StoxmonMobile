package com.stoxmon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleLight,
    onPrimaryContainer = Color.White,
    
    secondary = AccentPink,
    onSecondary = Color.White,
    secondaryContainer = AccentRed,
    onSecondaryContainer = Color.White,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = BackgroundCard,
    onSurface = TextPrimary,
    
    error = NegativeRed,
    onError = Color.White,
)

@Composable
fun StoxmonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
