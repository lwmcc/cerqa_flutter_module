package com.cerqa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.Navigation.BottomNavItem
import com.cerqa.ui.Navigation.TopNavItem
import com.cerqa.ui.resources.getAddChatIcon
import com.cerqa.ui.resources.getAddGroupIcon

/**
 * Main bottom navigation bar items (Android)
 * Note: This is the fallback for shared module - app module overrides with custom icons
 */
actual val navItems = listOf(
    BottomNavItem(
        route = AppDestination.Main.route,
        icon = Icons.Outlined.DirectionsCar,
        label = "Navigate",
        contentDescription = "Navigate"
    ),
    BottomNavItem(
        route = AppDestination.Chat.route,
        icon = Icons.Outlined.Sms,
        label = "Chat",
        contentDescription = "Chat"
    ),
    BottomNavItem(
        route = AppDestination.Notifications.route,
        icon = Icons.Outlined.Notifications,
        label = "Notifications",
        contentDescription = "Inbox"
    ),
)

/**
 * Main top navigation bar items
 */
actual val topNavItemsMain = listOf(
    TopNavItem(
        route = AppDestination.Contacts.route,
        iconComposable = {
            Icon(
                painter = getAddChatIcon(),
                contentDescription = "Add chat",
                tint = Color.Unspecified
            )
        },
        contentDescription = "Contacts screen",
    ),
    TopNavItem(
        route = AppDestination.Groups.route,
        iconComposable = {
            Icon(
                painter = getAddGroupIcon(),
                contentDescription = "Add group",
                tint = Color.Unspecified
            )
        },
        contentDescription = "Groups screen",
    ),
)

actual val topNavItemsContacts = listOf(
    TopNavItem(
        route = AppDestination.ContactsSearch.route,
        icon = Icons.Outlined.Search,
        contentDescription = "Search screen",
    )
)

actual val topNavItemsGroups = listOf(
    TopNavItem(
        route = AppDestination.GroupsAdd.route,
        icon = Icons.Outlined.Add,
        contentDescription = "Add Groups screen",
    )
)
