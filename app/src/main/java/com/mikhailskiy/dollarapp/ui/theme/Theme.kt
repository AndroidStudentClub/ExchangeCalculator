package com.mikhailskiy.dollarapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = green,
    onPrimary = black,
    background = darkBackground,
    onBackground = white,
    error = errorDark,
    surface = darkSurface,
    onSurface = white,
    onSecondary = white,
    surfaceVariant = darkBackgroundOnSecondary,
    tertiary = contentBlue
)

private val LightColorScheme = lightColorScheme(
    primary = green,
    onPrimary = white,
    background = bottomSheetBackground,
    onBackground = primaryText,
    error = errorLight,
    surface = white,
    onSurface = primaryText,
    onSecondary = primaryText,
    surfaceVariant = backgroundOnSecondary,
    tertiary = contentBlue
)

@Composable
fun DollarAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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