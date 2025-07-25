package com.cerqa.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.components.topNavItemsContacts
import com.cerqa.ui.components.topNavItemsGroups
import com.cerqa.ui.components.topNavItemsMain

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
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.contentDescription) },
                selected = item.route == currentRoute,
                onClick = {
                    onBottomNavClick(item.route)
                },
            )
        }
    }
}
