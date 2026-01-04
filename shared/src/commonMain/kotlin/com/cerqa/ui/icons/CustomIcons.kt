package com.cerqa.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Platform-specific custom icons
 */
expect object CustomIcons {
    @Composable
    fun carsNav(): Painter

    @Composable
    fun personsPlus(): Painter
}
