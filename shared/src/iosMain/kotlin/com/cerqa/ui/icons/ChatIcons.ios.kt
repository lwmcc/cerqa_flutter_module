package com.cerqa.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import carclub.shared.generated.resources.Res
import carclub.shared.generated.resources.archivebox
import carclub.shared.generated.resources.figure_walksvg
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun archiveBoxIcon(): Painter {
    return painterResource(Res.drawable.archivebox)
}

@Composable
actual fun figureWalkIcon(): Painter {
    return painterResource(Res.drawable.figure_walksvg)
}