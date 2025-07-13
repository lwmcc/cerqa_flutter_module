package com.cerqa.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.More
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.graphics.vector.ImageVector
import com.cerqa.ui.components.AppScreens

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
    items: List<TopNavItem>,
    onNavClick: () -> Unit,
    onTopNavClick: (String) -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        title = {
            Text(
                text = "Top Bar",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
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
