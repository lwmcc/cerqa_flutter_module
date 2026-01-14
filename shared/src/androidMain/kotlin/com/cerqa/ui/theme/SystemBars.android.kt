package com.cerqa.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun SystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDarkIcons: Boolean
) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect

        // Set status bar color
        window.statusBarColor = statusBarColor.toArgb()

        // Set navigation bar color
        window.navigationBarColor = navigationBarColor.toArgb()

        // Configure status bar icon color (dark or light)
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkIcons

        // Configure navigation bar icon color (dark or light)
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isDarkIcons
    }
}