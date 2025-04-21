package com.mccartycarclub.navigation

import androidx.navigation.NavHostController
import com.mccartycarclub.MainActivity.Companion.CONTACTS_SCREEN
import com.mccartycarclub.MainActivity.Companion.GROUPS_SCREEN
import com.mccartycarclub.MainActivity.Companion.SEARCH_SCREEN

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToContacts() {
        println("Shared ***** NAV ${navController.toString()}")
        navController.navigate(CONTACTS_SCREEN)
    }

    fun navigateToGroups() {
        navController.navigate(GROUPS_SCREEN)
    }

    fun navigateToSearch() {
        navController.navigate(SEARCH_SCREEN)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}