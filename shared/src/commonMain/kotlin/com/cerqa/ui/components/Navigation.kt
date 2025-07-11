package com.cerqa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import com.cerqa.ui.BottomNavItem

/**
 * Bottom navigation bar
 */
val navItems = listOf(
    BottomNavItem(
        route = "Home",
        icon = Icons.Filled.Home,
        label = "Home",
        contentDescription = "Home screen"
    ),
    BottomNavItem(
        route = "Chat",
        icon = Icons.AutoMirrored.Filled.Chat,
        label = "Chat",
        contentDescription = "Chat screen"
    ),
    BottomNavItem(
        route = "Notifications",
        icon = Icons.Filled.Notifications,
        label = "Notifications",
        contentDescription = "Notifications screen"
    ),
)