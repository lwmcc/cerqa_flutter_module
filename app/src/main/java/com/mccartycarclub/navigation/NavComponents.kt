package com.mccartycarclub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import com.cerqa.ui.components.topNavItemsContacts
import com.cerqa.ui.components.topNavItemsGroups
import com.cerqa.ui.components.topNavItemsMain
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.BottomNavItem
import com.cerqa.ui.Navigation.TopNavItem

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
