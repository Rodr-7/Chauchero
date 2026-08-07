package com.rodr.chauchero.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ChaucheroGreen,
    onPrimary = Color.White,
    primaryContainer = ChaucheroGreenContainerDark,
    onPrimaryContainer = ChaucheroMintNavigation,
    secondary = ChaucheroMintNavigation,
    onSecondary = Color.Black,
    background = ChaucheroBackgroundDark,
    onBackground = ChaucheroTextPrimaryDark,
    surface = ChaucheroSurfaceDark,
    onSurface = ChaucheroTextPrimaryDark,
    surfaceVariant = ChaucheroCardDark,
    onSurfaceVariant = ChaucheroTextPrimaryDark,
    secondaryContainer = ChaucheroCardDark,
    onSecondaryContainer = ChaucheroTextPrimaryDark,
    error = ChaucheroWarning,
    onError = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = ChaucheroGreen,
    onPrimary = Color.White,
    primaryContainer = ChaucheroGreenContainerLight,
    onPrimaryContainer = ChaucheroGreenContainerDark,
    secondary = ChaucheroMintNavigation,
    onSecondary = ChaucheroTextPrimaryLight,
    background = ChaucheroBackgroundLight,
    onBackground = ChaucheroTextPrimaryLight,
    surface = ChaucheroSurfaceLight,
    onSurface = ChaucheroTextPrimaryLight,
    surfaceVariant = ChaucheroCardLight,
    onSurfaceVariant = ChaucheroTextPrimaryLight,
    secondaryContainer = ChaucheroMintNavigation,
    onSecondaryContainer = ChaucheroTextPrimaryLight,
    error = ChaucheroWarning,
    onError = Color.White,
)

@Composable
fun ChaucheroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
