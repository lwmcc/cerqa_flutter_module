package com.cerqa.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Platform-specific icon painters for chat screen
 */
@Composable
expect fun getAddChatIcon(): Painter

@Composable
expect fun getAddGroupIcon(): Painter
