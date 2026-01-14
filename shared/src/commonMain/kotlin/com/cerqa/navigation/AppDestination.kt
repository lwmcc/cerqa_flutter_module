package com.cerqa.navigation

sealed class AppDestination(val route: String) {
    object Main : AppDestination("main")
    object Chat : AppDestination("chat")
    object Notifications : AppDestination("notifications")
    object Contacts : AppDestination("contacts")
    object ContactsSearch : AppDestination("contacts-search")
    object EditGroup : AppDestination("edit-group/{groupId}") {
        fun createRoute(groupId: String) = "edit-group/$groupId"
    }
    object Groups : AppDestination("groups")
    object GroupsAdd : AppDestination("groups-add")
    object Profile : AppDestination("profile")
    object Conversation : AppDestination("conversation/{contactId}/{userName}/{isGroup}") {
        fun createRoute(contactId: String, userName: String, isGroup: Boolean = false) =
            "conversation/$contactId/$userName/$isGroup"
    }
}