package com.cerqa.navigation

sealed class AppDestination(val route: String) {
    object Main : AppDestination("main")
    object Chat : AppDestination("chat")
    object Notifications : AppDestination("notifications")
    object Contacts : AppDestination("contacts")
    object ContactsSearch : AppDestination("contacts-search")
    object Groups : AppDestination("groups")
    object GroupsAdd : AppDestination("groups-add")
    object Profile : AppDestination("profile")
    object Conversation : AppDestination("conversation/{contactId}/{userName}") {
        fun createRoute(contactId: String, userName: String) = "conversation/$contactId/$userName"
    }
}