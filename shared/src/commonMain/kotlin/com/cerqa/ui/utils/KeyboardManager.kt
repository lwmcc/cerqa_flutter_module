package com.cerqa.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific keyboard management
 */
expect class KeyboardManager() {
    @Composable
    fun setupKeyboardHandling(
        onShow: () -> Unit,
        onHide: () -> Unit
    )

    @Composable
    fun addFocusModifier(modifier: Modifier): Modifier
}