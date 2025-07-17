package com.mccartycarclub.navigation

import androidx.navigation.NavHostController
import com.cerqa.ui.components.AppScreens

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToContacts() {
        navController.navigate(AppScreens.Contacts.route)
    }

    fun navigateToChat() {
        navController.navigate(AppScreens.Chat.route)
    }


    fun navigateToGroups() {
        navController.navigate(AppScreens.Groups.route)
    }

    fun navigateToNotifications() {
        navController.navigate(AppScreens.Notifications.route)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}
