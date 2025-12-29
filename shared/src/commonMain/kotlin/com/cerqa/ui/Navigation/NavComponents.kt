package com.cerqa.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.components.topNavItemsContacts
import com.cerqa.ui.components.topNavItemsGroups
import com.cerqa.ui.components.topNavItemsMain

data class BottomNavItem(
    val route: String,
    val icon: ImageVector? = null,
    val label: String,
    val contentDescription: String,
    val iconComposable: (@Composable () -> Unit)? = null
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
    } as List<TopNavItem>
}

@Composable
fun BottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onBottomNavClick: (String) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.iconComposable != null) {
                        item.iconComposable.invoke()
                    } else if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.contentDescription
                        )
                    }
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
