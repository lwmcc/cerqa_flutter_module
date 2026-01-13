package com.cerqa.ui.Navigation

import androidx.navigation.NavHostController
import com.cerqa.navigation.AppDestination

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateToContacts() {
        navController.navigate(AppDestination.Contacts.route) {
            launchSingleTop = true
        }
    }

    fun navigateToContactsSearch() {
        navController.navigate(AppDestination.ContactsSearch.route) {
            launchSingleTop = true
        }
    }

    // TODO: remove using tabs
    fun navigateToGroups() {
        navController.navigate(AppDestination.Groups.route) {
            launchSingleTop = true
        }
    }

    fun navigateToGroupsAdd() {
        navController.navigate(AppDestination.GroupsAdd.route) {
            launchSingleTop = true
        }
    }

    fun navigateToMain() {
        navController.navigate(AppDestination.Main.route) {
            launchSingleTop = true
        }
    }

    fun navigateToChat() {
        navController.navigate(AppDestination.Chat.route) {
            launchSingleTop = true
        }
    }

    fun navigateToNotifications() {
        navController.navigate(AppDestination.Notifications.route) {
            launchSingleTop = true
        }
    }

    fun navigateToProfile() {
        navController.navigate(AppDestination.Profile.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToEditGroup(groupId: String) {
        navController.navigate(AppDestination.EditGroup.createRoute(groupId)) {
            launchSingleTop = true
        }
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}
