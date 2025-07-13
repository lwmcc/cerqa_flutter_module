package com.cerqa.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.cerqa.ui.theme.PlatformThemeWrapper
import com.cerqa.viewmodels.MainViewModel
import org.koin.mp.KoinPlatform.getKoin
import platform.UIKit.UIViewController

fun mainViewController(userId: String, userName: String): UIViewController =
    ComposeUIViewController {
        val mainViewModel = getKoin().get<MainViewModel>()

        LaunchedEffect(Unit) {
            mainViewModel.setUserData(userId = userId, userName = userName)
        }

        PlatformThemeWrapper {
            StartScreen()
        }
    }
