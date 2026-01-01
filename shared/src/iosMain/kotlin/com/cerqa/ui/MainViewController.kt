package com.cerqa.ui

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.ExperimentalComposeUiApi
import com.cerqa.ui.screens.App
import platform.UIKit.UIViewController

@OptIn(ExperimentalComposeUiApi::class)
fun createComposeViewController(): UIViewController =
    ComposeUIViewController(
        configure = {
            // Disable strict plist check for high refresh rate devices
            enforceStrictPlistSanityCheck = false
            // Use opaque rendering to fix Metal rendering crashes
            opaque = true
        }
    ) {
        App()
    }
