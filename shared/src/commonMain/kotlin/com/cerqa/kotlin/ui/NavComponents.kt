package com.cerqa.kotlin.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.navigation.NavHostController


@Composable
fun BottomBar(navController: NavHostController) {

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            onClick = {

            },
            selected = true,
        )
        BottomNavigationItem(
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
            onClick = {

            },
            selected = false,
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            onClick = {

            },
            selected = false,
        )
    }
}
