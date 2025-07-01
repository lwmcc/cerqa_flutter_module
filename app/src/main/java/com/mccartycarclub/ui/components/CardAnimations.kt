package com.mccartycarclub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun TextRowAnimation(visible: Boolean, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally {
            with(density) { -40.dp.roundToPx() }
        } + expandHorizontally(
            expandFrom = Alignment.Start
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(durationMillis = 1000),
        )
    ) {
        content()
    }
}
