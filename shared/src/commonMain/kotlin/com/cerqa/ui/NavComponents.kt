package com.cerqa.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    text: String,
    items: List<TopNavItem>,
    onNavClick: () -> Unit,
    onTopNavClick: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        title = {
            TextField(
                value = "", // TODO: add text
                onValueChange = onQueryChanged,
                placeholder = { Text(text = text) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        },
        actions = {
            items.forEach { item ->
                IconButton(onClick = {
                    onTopNavClick(item.route)
                }) {
                    Icon(item.icon, contentDescription = item.contentDescription)
                }
            }
        }
    )

}
