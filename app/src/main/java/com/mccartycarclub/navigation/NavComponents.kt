package com.mccartycarclub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
)

data class TopNavItem(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String,
)

fun getTopNavItems(route: String?): List<TopNavItem> {
    return when (route) {
        AppDestination.Main.route -> topNavItemsMain
        AppDestination.Contacts.route -> topNavItemsContacts
        AppDestination.ContactsSearch.route -> topNavItemsContacts
        AppDestination.GroupsAdd.route -> topNavItemsGroups
        else -> emptyList()
    }
}

@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onBottomNavClick: (String) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface // equivalent to backgroundColor
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                selected = item.route == currentRoute,
                onClick = { onBottomNavClick(item.route) },
                label = {
                    Text(text = item.contentDescription)
                },
                alwaysShowLabel = true
            )
        }
    }
}
