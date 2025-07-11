package com.cerqa.kotlin.ui.theme

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformThemeWrapper(content: @Composable (() -> Unit))