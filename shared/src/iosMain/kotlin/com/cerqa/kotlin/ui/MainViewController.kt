package com.cerqa.kotlin.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.cerqa.kotlin.viewmodels.MainViewModel
import platform.UIKit.UIViewController

fun mainViewController(userId: String, userName: String): UIViewController =
    ComposeUIViewController {
        val mainViewModel = remember { MainViewModel() }

        LaunchedEffect(Unit) {
            mainViewModel.setUserData(userId = userId, userName = userName)
        }

        StartScreen()
    }