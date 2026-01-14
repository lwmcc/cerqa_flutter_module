package com.cerqa.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Sets the system status bar and navigation bar colors.
 *
 * @param statusBarColor Color for the status bar (top)
 * @param navigationBarColor Color for the navigation bar (bottom)
 * @param isDarkIcons Whether to use dark icons on the status bar (for light backgrounds)
 */
@Composable
expect fun SystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color = statusBarColor,
    isDarkIcons: Boolean = false
)