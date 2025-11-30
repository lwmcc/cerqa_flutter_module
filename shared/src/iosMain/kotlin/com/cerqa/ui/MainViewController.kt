package com.cerqa.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.cerqa.ui.screens.App
import platform.UIKit.UIViewController

fun createComposeViewController(): UIViewController =
    ComposeUIViewController(
        configure = {
            // TODO: add this in xcode
            // Disable strict plist check for high refresh rate devices
            enforceStrictPlistSanityCheck = false
        }
    ) {
        App()
    }
