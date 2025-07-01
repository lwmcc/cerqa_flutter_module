package com.mccartycarclub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mccartycarclub.domain.UiUserMessage
import kotlin.math.abs

@Composable
fun BannerMessage(
    message: UiUserMessage?,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(message != null) }

    LaunchedEffect(message) {
        visible = message != null
    }
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
        message?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (abs(dragAmount) > 100f) {
                                visible = false
                                onDismiss()
                            }
                        }
                    }
            ) {
                Text(
                    // TODO: pass resource strings in
                    text = when (message) {
                        UiUserMessage.INVITE_SENT -> "Invite sent"
                        UiUserMessage.NO_INTERNET -> "No internet"
                        UiUserMessage.NETWORK_ERROR -> "An error occurred"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}