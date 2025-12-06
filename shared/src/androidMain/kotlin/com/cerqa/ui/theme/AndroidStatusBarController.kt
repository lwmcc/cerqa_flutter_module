package com.cerqa.ui.theme

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat

class AndroidStatusBarController(private val activity: Activity) : StatusBarController {
    override fun setStatusBarColor(colorHex: Long, darkIcons: Boolean) {
        val window = activity.window
        val color = (colorHex or 0xFF000000).toInt() // Ensure alpha is FF
        window.statusBarColor = color

        // Set status bar icon color (light or dark)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = darkIcons
    }
}