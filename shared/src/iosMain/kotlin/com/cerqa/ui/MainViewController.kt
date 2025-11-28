package com.cerqa.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.cerqa.ui.components.App
import platform.UIKit.UIViewController

fun createComposeViewController(): UIViewController =
    ComposeUIViewController {
        App()
    }
