package com.cerqa.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun PlatformThemeWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        // Use the color schemes defined in Theme.kt via AppTheme
        null
    }

    AppTheme(
        darkTheme = darkTheme,
        colorSchemeOverride = colorScheme,
        content = {
            // Capture the color scheme inside the theme
            val currentColorScheme = androidx.compose.material3.MaterialTheme.colorScheme

            // Set status bar color to match the surface/background
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    window.statusBarColor = currentColorScheme.surface.toArgb()
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = !darkTheme
                }
            }

            // Render the actual content
            content()
        }
    )
}