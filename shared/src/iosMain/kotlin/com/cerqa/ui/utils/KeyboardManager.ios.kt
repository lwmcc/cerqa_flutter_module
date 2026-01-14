package com.cerqa.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

actual class KeyboardManager {
    @Composable
    actual fun setupKeyboardHandling(
        onShow: () -> Unit,
        onHide: () -> Unit
    ) {
        // iOS handles keyboard automatically, no manual intervention needed
        LaunchedEffect(Unit) {
            try {
                onShow()
            } catch (e: Exception) {
                println("KeyboardManager (iOS): Error in setup: ${e.message}")
            }
        }
    }

    @Composable
    actual fun addFocusModifier(modifier: Modifier): Modifier {
        // On iOS, focus is handled automatically by the system
        // Return the modifier unchanged to avoid iOS-specific focus issues
        return modifier
    }
}