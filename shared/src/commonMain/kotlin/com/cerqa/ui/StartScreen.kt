package com.cerqa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cerqa.ui.components.AppScreens
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.ui.components.navItems
import com.cerqa.ui.components.navToScreen
import com.cerqa.ui.components.topNavItemsMain
import com.cerqa.viewmodels.ContactsViewModel

@Composable
fun StartScreen(
    onNavHostReady: suspend (NavController) -> Unit = {},
    mainViewModel: MainViewModel = koinInject(),
    contactsViewModel: ContactsViewModel = koinInject(),
    navController: NavHostController = rememberNavController(),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    },
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    Scaffold(
        topBar = {
            TopBar(
                items = topNavItemsMain,
                onNavClick = {
                    // TODO: nav to profile
                },
                onTopNavClick = { route ->
                    navToScreen(route, currentRoute.toString(), navActions)
                },
            )
        },

        bottomBar = {
            BottomBar(
                items = navItems,
                currentRoute = currentRoute,
                onBottomNavClick = { route ->
                    navToScreen(route, currentRoute.toString(), navActions)
                },
            )
        }
    ) {

        NavHost(navController = navController, startDestination = AppScreens.Main.route) {
            composable(AppScreens.Main.route) {
                MainScreen(mainViewModel)
            }

            composable(AppScreens.Chat.route) {
                ChatScreen()
            }

            composable(AppScreens.Notifications.route) {
                NotificationScreen()
            }

            composable(AppScreens.Contacts.route) {
                ContactsScreen(contactsViewModel)
            }
            composable(AppScreens.Groups.route) {
                GroupsScreen()
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "larry composable test",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(onClick = {
            mainViewModel.getUserData()
        }) {
            Text("Prefs Read/Write Test")
        }
    }
}


@Composable
fun ChatScreen() {
    Text("CHAT")
}

@Composable
fun NotificationScreen() {
    Text("NOTIFICATIONS")
}

@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel) {
    Text("CONTACTS")
}

@Composable
fun GroupsScreen() {
    Text("GROUPS")
}
