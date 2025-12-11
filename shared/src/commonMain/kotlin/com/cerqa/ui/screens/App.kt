package com.cerqa.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cerqa.navigation.AppDestination
import com.cerqa.models.Contact
import com.cerqa.ui.Navigation.AppNavigationActions
import com.cerqa.ui.Navigation.BottomBar
import com.cerqa.ui.Navigation.TopBar
import com.cerqa.ui.Navigation.getTopNavItems
import com.cerqa.ui.animations.slideInFromRight
import com.cerqa.ui.animations.slideOutToRight
import com.cerqa.ui.components.navItems
import com.cerqa.viewmodels.ApolloContactsViewModel
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.viewmodels.SearchViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    },
    searchViewModel: SearchViewModel = koinInject(),
    contactsViewModel: ContactsViewModel = koinInject(),
    mainViewModel: MainViewModel = koinInject(),
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topNavItems = getTopNavItems(currentRoute)

    LaunchedEffect(Unit) {
        mainViewModel.fetchUser()
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                if (currentRoute == null || currentRoute == "main") {
                    TopAppBar( // TODO: move code to a function
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        navigationIcon = {
                            // Profile icon on the left
                            IconButton(
                                onClick = {
                                    navActions.navigateToProfile()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        title = {
                            Text("Main")
                        },
                        actions = {
                            // Search icon - only icon on home screen
                            IconButton(onClick = { navActions.navigateToContactsSearch() }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    )
                } else {
                    TopBar(
                        searchViewModel = searchViewModel,
                        currentRoute = currentRoute,
                        text = "SEARCH FIELD", // TODO: stringResource(id = R.string.search_field),
                        items = topNavItems,
                        onNavClick = {
                            navActions.navigateToProfile()
                        },
                        onBackClick = {
                            navActions.popBackStack()
                        },
                        onTopNavClick = { route ->
                            // TODO: nav to route
                        },
                        onQueryChanged = { query ->

                        },
                    )
                }
            },

            bottomBar = {
                BottomBar(
                    items = navItems,
                    currentRoute = currentRoute,
                    onBottomNavClick = { route ->
                        navController.navigate(route) {
                            popUpTo(AppDestination.Main.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Main.route,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable(AppDestination.Chat.route) {
                    Chat(
                        onNavigateToContacts = {
                            navActions.navigateToContacts()
                        }
                    )
                }
                composable(AppDestination.Contacts.route) {
                    Contacts(
                        searchViewModel = searchViewModel,
                        contactsViewModel = contactsViewModel
                    )
                }
                composable(AppDestination.Groups.route) {
                    Groups()
                }
                composable(AppDestination.Notifications.route) {
                    Inbox()
                }
                composable(AppDestination.Main.route) {
                    Main()
                }
                composable(
                    AppDestination.Profile.route,
                    enterTransition = { slideInFromRight() },
                    exitTransition = { slideOutToRight() }
                ) {
                    Profile(
                        onDismiss = { navActions.popBackStack() }
                    )
                }
                composable(AppDestination.ContactsSearch.route) {
                    Search()
                }
            }
        }
    }
}

// TODO: shorten functin length
@Composable
fun ContactsScreen(
    viewModel: ApolloContactsViewModel = koinInject()
) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Fetch contacts
    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Contacts",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Contacts list
        if (!isLoading && contacts.isEmpty() && error == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No contacts found",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { contact ->
                    ContactCard(contact = contact)
                }
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = contact.name ?: contact.userName ?: "Unknown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            contact.phoneNumber?.let { phone ->
                Text(
                    text = phone,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            contact.userName?.let { userName ->
                Text(
                    text = "@$userName",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}