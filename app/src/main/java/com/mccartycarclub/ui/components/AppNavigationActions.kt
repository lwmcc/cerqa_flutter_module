package com.mccartycarclub.ui.components

import androidx.navigation.NavHostController
import com.cerqa.ui.components.AppScreens

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToMain() {
        while (navController.popBackStack()) { /* no-op */ }
        navController.navigate(AppScreens.Main.route) {
            launchSingleTop = true
        }
    }

    fun navigateToChat() {
        navController.navigate(AppScreens.Chat.route)
    }

    fun navigateToNotifications() {
        navController.navigate(AppScreens.Notifications.route)
    }

    fun navigateToContacts() {
        navController.navigate(AppScreens.Contacts.route)
    }

    fun navigateToGroups() {
        navController.navigate(AppScreens.Groups.route)
    }

    fun navigateToSearch() {
        // navController.navigate(SEARCH_SCREEN)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}
