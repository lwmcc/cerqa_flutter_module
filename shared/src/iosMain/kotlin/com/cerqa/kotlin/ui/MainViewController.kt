package com.cerqa.kotlin.ui

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController = ComposeUIViewController {
    StartScreen()
}