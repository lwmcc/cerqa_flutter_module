package com.cerqa.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import carclub.shared.generated.resources.Res
import carclub.shared.generated.resources.add_chat
import carclub.shared.generated.resources.add_group
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun getAddChatIcon(): Painter {
    return painterResource(Res.drawable.add_chat)
}

@Composable
actual fun getAddGroupIcon(): Painter {
    return painterResource(Res.drawable.add_group)
}
