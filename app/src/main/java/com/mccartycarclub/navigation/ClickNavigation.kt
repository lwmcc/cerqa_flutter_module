package com.mccartycarclub.navigation

sealed class ClickNavigation {
    data object NavToGroups : ClickNavigation()
    data object NavToContacts : ClickNavigation()
    data object PopBackstack : ClickNavigation()
}