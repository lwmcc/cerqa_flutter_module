package com.mccartycarclub.ui.components

import com.mccartycarclub.navigation.AppDestination
import com.mccartycarclub.navigation.AppNavigationActions

fun navToScreen(
    route: String,
    navActions: AppNavigationActions,
    onChatClick: () -> Unit,
) {
    when (route) {
        AppDestination.Contacts.route -> navActions.navigateToContacts()
        AppDestination.ContactsSearch.route -> navActions.navigateToContactsSearch()
        AppDestination.Groups.route -> navActions.navigateToGroups()
        AppDestination.GroupsAdd.route -> navActions.navigateToGroupsAdd()
        AppDestination.Main.route -> navActions.navigateToMain()
        AppDestination.Chat.route -> onChatClick() // navActions.navigateToChat()
        AppDestination.Notifications.route -> navActions.navigateToNotifications()
    }
}
