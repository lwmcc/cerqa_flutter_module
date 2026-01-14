package com.cerqa.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

actual class KeyboardManager {
    @Composable
    actual fun setupKeyboardHandling(
        onShow: () -> Unit,
        onHide: () -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            try {
                onShow()
                keyboardController?.show()
            } catch (e: Exception) {
                println("KeyboardManager (Android): Error showing keyboard: ${e.message}")
            }
        }
    }

    @Composable
    actual fun addFocusModifier(modifier: Modifier): Modifier {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                println("KeyboardManager (Android): Error requesting focus: ${e.message}")
            }
        }

        return modifier.focusRequester(focusRequester)
    }
}