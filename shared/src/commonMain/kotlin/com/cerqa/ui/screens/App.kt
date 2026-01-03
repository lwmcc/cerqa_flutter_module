package com.cerqa.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.cerqa.data.Preferences
import com.cerqa.auth.AuthTokenProvider
import org.koin.compose.koinInject
import com.cerqa.ui.resources.getAddChatIcon
import com.cerqa.ui.resources.getAddGroupIcon

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
    bottomNavItems: List<com.cerqa.ui.Navigation.BottomNavItem> = navItems,
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topNavItems = getTopNavItems(currentRoute)

    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var chatTabIndex by remember { mutableStateOf(0) } // 0 = Chats, 1 = Groups

    val unreadNotificationCount by mainViewModel.unreadNotificationCount.collectAsState()

    val bottomNavItemsWithBadge = remember(unreadNotificationCount) {
        bottomNavItems.map { item ->
            if (item.route == AppDestination.Notifications.route) {
                item.copy(badgeCount = unreadNotificationCount)
            } else {
                item
            }
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.fetchUser()
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                when (currentRoute) {
                    null, AppDestination.Main.route -> {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = { navActions.navigateToProfile() },
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
                            title = { Text("Main") },
                            actions = {
                                IconButton(onClick = { navActions.navigateToContactsSearch() }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        )
                    }

                    AppDestination.Chat.route -> {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            navigationIcon = {
                                IconButton(onClick = { navActions.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            title = {
                                // TODO: set this up just copied and pasted from contacts
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { newValue ->
                                        searchQuery = newValue
                                        searchViewModel.onQueryChange(newValue)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(text = "Search users")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Search"
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "Clear",
                                                modifier = Modifier.clickable {
                                                    searchQuery = ""
                                                    searchViewModel.onQueryChange("")
                                                }
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    )
                                )
                            },
                            actions = {
                                IconButton(onClick = { navActions.navigateToContacts() }) {
                                    Icon(
                                        painter = if (chatTabIndex == 1) getAddGroupIcon() else getAddChatIcon(),
                                        contentDescription = if (chatTabIndex == 1) "Add Group Member" else "Add Contact",
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        )
                    }

                    AppDestination.Contacts.route -> {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            navigationIcon = {
                                IconButton(onClick = { navActions.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            title = {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { newValue ->
                                        searchQuery = newValue
                                        searchViewModel.onQueryChange(newValue)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(text = "Search users")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Search"
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "Clear",
                                                modifier = Modifier.clickable {
                                                    searchQuery = ""
                                                    searchViewModel.onQueryChange("")
                                                }
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    )
                                )
                            },
                            actions = {
                                IconButton(onClick = { /* TODO: Add Contact Action */ }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Contact")
                                }
                            }
                        )
                    }

                    else -> {
                        TopBar(
                            searchViewModel = searchViewModel,
                            currentRoute = currentRoute,
                            text = "SEARCH FIELD",
                            items = topNavItems,
                            onNavClick = { navActions.navigateToProfile() },
                            onBackClick = { navActions.popBackStack() },
                            onTopNavClick = { route -> /* TODO: nav to route */ },
                            onQueryChanged = { query -> }
                        )
                    }
                }
            },

            bottomBar = {
                BottomBar(
                    items = bottomNavItemsWithBadge,
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
                        selectedTabIndex = chatTabIndex,
                        onTabChange = { chatTabIndex = it },
                        onNavigateToContacts = {
                            navActions.navigateToContacts()
                        },
                        onNavigateToConversation = { contactId, userName ->
                            navController.navigate(
                                AppDestination.Conversation.createRoute(contactId, userName)
                            )
                        }
                    )
                }
                composable(AppDestination.Contacts.route) {
                    Contacts(
                        searchQuery = searchQuery,
                        searchViewModel = searchViewModel,
                        contactsViewModel = contactsViewModel,
                        mainViewModel = mainViewModel,
                        onNavigateToConversation = { contactId, userName ->
                            navController.navigate(
                                AppDestination.Conversation.createRoute(contactId, userName)
                            )
                        }
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
                composable(
                    route = AppDestination.Conversation.route,
                    arguments = listOf(
                        navArgument("contactId") { type = NavType.StringType },
                        navArgument("userName") { type = NavType.StringType }
                    ),
                    enterTransition = { slideInFromRight() },
                    exitTransition = { slideOutToRight() }
                ) { backStackEntry ->
                    // Get receiver ID from navigation arguments
                    val receiverId = backStackEntry.arguments?.get("contactId") as? String ?: ""

                    println("App.kt ***** RECEIVER ID (from navigation): $receiverId")

                    Conversation(
                        receiverId = receiverId
                    )
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