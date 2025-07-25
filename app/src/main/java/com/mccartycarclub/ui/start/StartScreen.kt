package com.mccartycarclub.ui.start

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amplifyframework.ui.authenticator.SignedInState
import com.cerqa.navigation.AppDestination
import com.cerqa.ui.BottomBar
import com.cerqa.ui.components.navItems
import com.cerqa.ui.getTopNavItems
import com.mccartycarclub.R
import com.mccartycarclub.domain.model.SmsMessage
import com.mccartycarclub.navigation.AppNavigationActions
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.navigation.TopBar
import com.mccartycarclub.ui.components.ChatScreen
import com.mccartycarclub.ui.components.GroupsAddScreen
import com.mccartycarclub.ui.components.GroupsScreen
import com.mccartycarclub.ui.components.NotificationScreen
import com.mccartycarclub.ui.components.navToScreen
import com.mccartycarclub.ui.contacts.ContactsScreen
import com.mccartycarclub.ui.contacts.ContactsSearchScreen
import com.mccartycarclub.ui.viewmodels.ContactsViewModel
import com.mccartycarclub.ui.viewmodels.MainViewModel
import com.mccartycarclub.ui.viewmodels.SearchViewModel

@Composable
fun StartScreen(
    mainViewModel: MainViewModel,
    searchViewModel: SearchViewModel = hiltViewModel(),
    state: SignedInState,
    navController: NavHostController = rememberNavController(),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    },
    topBarClick: (ClickNavigation) -> Unit,
    sendSms: (SmsMessage) -> Unit,
) {

    val contactsViewModel: ContactsViewModel = hiltViewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topNavItems = getTopNavItems(currentRoute)

    Scaffold(
        topBar = {
            TopBar(
                searchViewModel = searchViewModel,
                currentRoute = currentRoute,
                text = stringResource(id = R.string.search_field),
                items = topNavItems,
                onNavClick = {
                    // TODO: nav to profile
                },
                onBackClick = {
                    navActions.popBackStack()
                },
                onTopNavClick = { route -> navToScreen(route, navActions) },
                onQueryChanged = { query ->

                },
            )
        },

        bottomBar = {
            BottomBar(
                items = navItems,
                currentRoute = currentRoute,
                onBottomNavClick = { route -> navToScreen(route, navActions) },
            )
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = AppDestination.Main.route) {
            composable(AppDestination.Main.route) {
                MainScreen(mainViewModel)
            }

            composable(AppDestination.Chat.route) {
                ChatScreen()
            }

            composable(AppDestination.Notifications.route) {
                NotificationScreen()
            }

            composable(AppDestination.Contacts.route) {
                ContactsScreen(
                    paddingValues,
                    contactsViewModel,
                    topBarClick = {
                        println("ICON CLICKED")
                    })
            }

            composable(AppDestination.ContactsSearch.route) {
                ContactsSearchScreen(
                    searchViewModel = searchViewModel,
                    sendSms = sendSms,
                    topBarClick = topBarClick,
                )
            }

            composable(AppDestination.Groups.route) {
                GroupsScreen()
            }

            composable(AppDestination.GroupsAdd.route) {
                GroupsAddScreen()
            }
        }
    }
}
