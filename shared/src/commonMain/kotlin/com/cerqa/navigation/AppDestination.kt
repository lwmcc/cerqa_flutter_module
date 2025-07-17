package com.cerqa.navigation

sealed class AppDestination(val route: String) {
    object Main : AppDestination("main")
    object Chat : AppDestination("chat")
    object Notifications : AppDestination("notifications")
    object Contacts : AppDestination("contacts")
    object ContactsSearch : AppDestination("contacts-search")
    object Groups : AppDestination("groups")
    object GroupsAdd : AppDestination("groups-add")
}