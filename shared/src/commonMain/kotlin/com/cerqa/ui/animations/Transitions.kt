package com.cerqa.ui.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

fun AnimatedContentTransitionScope<*>.slideInFromRight(): EnterTransition {
    return fadeIn(
        animationSpec = tween(300, easing = LinearEasing)
    ) + slideIntoContainer(
        animationSpec = tween(300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
    )
}

fun AnimatedContentTransitionScope<*>.slideOutToRight(): ExitTransition {
    return fadeOut(
        animationSpec = tween(300, easing = LinearEasing)
    ) + slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.End
    )
}