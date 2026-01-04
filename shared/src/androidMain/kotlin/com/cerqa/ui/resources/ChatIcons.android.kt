package com.cerqa.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
actual fun getAddChatIcon(): Painter {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier("add_chat", "drawable", context.packageName)
    return painterResource(resId)
}

@Composable
actual fun getAddGroupIcon(): Painter {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier("add_group", "drawable", context.packageName)
    return painterResource(resId)
}
