package com.cerqa.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.BottomNavItem
import com.cerqa.ui.TopNavItem

/**
 * Main bottom navigation bar items
 */
val navItems = listOf(
    BottomNavItem(
        route = AppDestination.Main.route,
        icon = Icons.Outlined.Home,
        label = "Home",
        contentDescription = "Home screen"
    ),
    BottomNavItem(
        route = AppDestination.Chat.route,
        icon = Icons.AutoMirrored.Outlined.Chat,
        label = "Chat",
        contentDescription = "Chat screen"
    ),
    BottomNavItem(
        route = AppDestination.Notifications.route,
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
        route = AppDestination.Contacts.route,
        icon = Icons.Outlined.Contacts,
        contentDescription = "Contacts screen",
    ),
    TopNavItem(
        route = AppDestination.Groups.route,
        icon = Icons.Outlined.Groups2,
        contentDescription = "Groups screen",
    ),
)

val topNavItemsContacts = listOf(
    TopNavItem(
        route = AppDestination.ContactsSearch.route,
        icon = Icons.Outlined.Search,
        contentDescription = "Search screen",
    )
)

val topNavItemsGroups = listOf(
    TopNavItem(
        route = AppDestination.GroupsAdd.route,
        icon = Icons.Outlined.Add,
        contentDescription = "Add Groups screen",
    )
)