package com.cerqa.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import carclub.shared.generated.resources.Res
import carclub.shared.generated.resources.add_chat
import carclub.shared.generated.resources.add_group
import carclub.shared.generated.resources.bell_nav
import carclub.shared.generated.resources.cars_nav
import carclub.shared.generated.resources.chat_nav
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.Navigation.BottomNavItem
import com.cerqa.ui.Navigation.TopNavItem
import org.jetbrains.compose.resources.painterResource

/**
 * Main bottom navigation bar items (iOS)
 */
actual val navItems = listOf(
    BottomNavItem(
        route = AppDestination.Main.route,
        iconComposable = {
            Icon(
                painter = painterResource(Res.drawable.cars_nav),
                contentDescription = "Navigate",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        label = "Navigate",
        contentDescription = "Navigate"
    ),
    BottomNavItem(
        route = AppDestination.Chat.route,
        iconComposable = {
            Icon(
                painter = painterResource(Res.drawable.chat_nav),
                contentDescription = "Chat",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        label = "Chat",
        contentDescription = "Chat"
    ),
    BottomNavItem(
        route = AppDestination.Notifications.route,
        iconComposable = {
            Icon(
                painter = painterResource(Res.drawable.bell_nav),
                contentDescription = "Inbox",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
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
                painter = painterResource(Res.drawable.add_chat),
                contentDescription = "Add chat",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        contentDescription = "Contacts screen",
    ),
    TopNavItem(
        route = AppDestination.Groups.route,
        iconComposable = {
            Icon(
                painter = painterResource(Res.drawable.add_group),
                contentDescription = "Add group",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
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
