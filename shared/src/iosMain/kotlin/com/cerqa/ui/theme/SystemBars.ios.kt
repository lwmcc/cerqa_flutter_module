package com.cerqa.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun SystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDarkIcons: Boolean
) {
    // Note: iOS status bar styling is controlled by the Info.plist file
    // and cannot be changed dynamically at runtime in the same way as Android
    // The status bar background color is determined by the view hierarchy beneath it
    // Since the TopAppBar extends into the safe area, it will provide the background color
}