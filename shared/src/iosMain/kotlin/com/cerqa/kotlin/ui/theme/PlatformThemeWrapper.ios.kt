package com.cerqa.kotlin.ui.theme

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformThemeWrapper(content: @Composable (() -> Unit)) {
    AppTheme(content = content)
}