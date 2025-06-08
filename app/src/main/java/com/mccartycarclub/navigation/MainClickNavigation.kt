package com.mccartycarclub.navigation

sealed class ClickNavigation {
    data object NavToGroups : ClickNavigation()
    data object NavToContacts : ClickNavigation()
    data object NavToSearch : ClickNavigation()
    data object NavToNotifications : ClickNavigation()
    data object PopBackstack : ClickNavigation()
}
