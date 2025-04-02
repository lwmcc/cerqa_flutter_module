package com.mccartycarclub.navigation

import androidx.navigation.NavHostController
import com.mccartycarclub.MainActivity.Companion.CONTACTS_SCREEN
import com.mccartycarclub.MainActivity.Companion.GROUPS_SCREEN

class AppNavigationActions(private val navController: NavHostController) {
    fun navigateToContacts() {
        navController.navigate(CONTACTS_SCREEN)
    }

    fun navigateToGroups() {
        navController.navigate(GROUPS_SCREEN)
    }
}