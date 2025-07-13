package com.cerqa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import com.cerqa.ui.AppNavigationActions
import com.cerqa.ui.BottomNavItem
import com.cerqa.ui.TopNavItem

sealed class AppScreens(val route: String) {
    object Main : AppScreens("main")
    object Chat : AppScreens("chat")
    object Notifications : AppScreens("notifications")
    object Contacts : AppScreens("contacts")
    object Groups : AppScreens("groups")
}

/**
 * Main bottom navigation bar items
 */
val navItems = listOf(
    BottomNavItem(
        route = AppScreens.Main.route,
        icon = Icons.Outlined.Home,
        label = "Home",
        contentDescription = "Home screen"
    ),
    BottomNavItem(
        route = AppScreens.Chat.route,
        icon = Icons.AutoMirrored.Outlined.Chat,
        label = "Chat",
        contentDescription = "Chat screen"
    ),
    BottomNavItem(
        route = AppScreens.Notifications.route,
        icon = Icons.Outlined.Notifications,
        label = "Notifications",
        contentDescription = "Notifications screen"
    ),
)

/**
 * Main top navigation bar items
 */
val topNavItemsMain = listOf(
    TopNavItem(
        route = AppScreens.Contacts.route,
        icon = Icons.Outlined.Contacts,
        contentDescription = "Contacts screen",
    ),
    TopNavItem(
        route = AppScreens.Groups.route,
        icon = Icons.Outlined.Groups2,
        contentDescription = "Groups screen",
    ),
)

fun navToScreen(
    route: String,
    currentRoute: String,
    navActions: AppNavigationActions,
) {
    when (route) {
        AppScreens.Main.route -> if (route != currentRoute) {
            navActions.navigateToMain()
        }


        AppScreens.Chat.route -> if (route != currentRoute) {
            navActions.navigateToChat()
        }

        AppScreens.Notifications.route -> if (route != currentRoute) {
            navActions.navigateToNotifications()
        }

        AppScreens.Contacts.route -> if (route != currentRoute) {
            navActions.navigateToContacts()
        }

        AppScreens.Groups.route -> if (route != currentRoute) {
            navActions.navigateToGroups()
        }
    }
}