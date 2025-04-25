package com.mccartycarclub.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.MainActivity.Companion.CONTACTS_SCREEN
import com.mccartycarclub.MainActivity.Companion.GROUPS_SCREEN
import com.mccartycarclub.MainActivity.Companion.MAIN_SCREEN
import com.mccartycarclub.MainActivity.Companion.SEARCH_SCREEN
import com.mccartycarclub.R
import com.mccartycarclub.navigation.AppNavigationActions
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.CurrentContact
import com.mccartycarclub.repository.NetResult
import com.mccartycarclub.repository.ReceivedContactInvite
import com.mccartycarclub.repository.SentContactInvite
import com.mccartycarclub.ui.callbacks.connectionclicks.ConnectionEvent
import com.mccartycarclub.ui.viewmodels.ContactsViewModel
import com.mccartycarclub.ui.viewmodels.MainViewModel
import com.mccartycarclub.utils.fetchUserId


@Composable
fun StartScreen(
    acceptInvite: () -> Unit,
    inviteContact: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    }
) {
    NavHost(navController = navController, startDestination = MAIN_SCREEN) {
        composable(MAIN_SCREEN) {
            AppAuthenticator(
                acceptInvite,
                inviteContact,
                topBarClick = {
                    navToScreen(it, navActions)
                }
            )
        }

        composable(CONTACTS_SCREEN) { backStackEntry ->
            Contacts(topBarClick = {
                navToScreen(it, navActions)
            })
        }

        composable(GROUPS_SCREEN) { backStackEntry ->
            Groups(topBarClick = {
                navToScreen(it, navActions)
            })
        }

        composable(SEARCH_SCREEN) { backStackEntry ->
            Search(topBarClick = {
                navToScreen(it, navActions)
            })
        }
    }
}

private fun navToScreen(
    clickNavigation: ClickNavigation,
    navActions: AppNavigationActions,
) {
    when (clickNavigation) {
        ClickNavigation.NavToContacts -> {
            navActions.navigateToContacts()
        }

        ClickNavigation.NavToGroups -> {
            navActions.navigateToGroups()
        }

        ClickNavigation.NavToSearch -> {
            navActions.navigateToSearch()
        }

        ClickNavigation.PopBackstack -> {
            navActions.popBackStack()
        }
    }
}

@Composable
fun AppAuthenticator(
    acceptInvite: () -> Unit,
    inviteContact: (String) -> Unit,
    topBarClick: (ClickNavigation) -> Unit,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopBar(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            com.amplifyframework.ui.authenticator.ui.Authenticator { state ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Hello ${state.user.username}!",
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Amplify.Auth.signOut { }
                        },
                    ) {
                        Text(text = "Sign Out")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                            Amplify.Auth.fetchUserAttributes({ attributes ->
                                val userId =
                                    attributes.firstOrNull { it.key.keyString == "sub" }?.value

                                if (userId != null) {
                                    mainViewModel.setLoggedInUserId(userId)
                                }

                                println("MainActivity ***** USER ID $userId")
                                // TODO: create test users
                                // testUser1  testUser2

                                Amplify.API.mutate(
                                    ModelMutation.create(testUser2(userId!!)),
                                    { response ->
                                        Log.i("MainActivity", "User created: ${response.data}")
                                        println("MainActivity ***** ERROR ${response.hasErrors()}")
                                        println("MainActivity ***** ERROR ${response.errors}")
                                    },
                                    { error ->
                                        Log.e("MainActivity", "User creation failed", error)
                                    }
                                )
                            }, { error ->
                                Log.e(
                                    "MainActivity *****", "Failed to fetch user attributes", error
                                )
                            })
                        }) {
                        Text(text = "Create User")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                        }) {
                        Text(text = "Add Contact")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            inviteContact(state.user.userId)
                        }) {
                        Text(text = "Send Invite")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            acceptInvite
                        }) {
                        Text(text = "Accept Invite")
                    }

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },
        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToGroups)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_groups), // Use your drawable resource
                    contentDescription = "Localized description"
                )
            }
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToContacts)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_contacts), // Use your drawable resource
                    contentDescription = "Localized description"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarContacts(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },
        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToSearch)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_search),
                    contentDescription = "Localized description"
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { topBarClick(ClickNavigation.PopBackstack) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarGroups(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },

        navigationIcon = {
            IconButton(onClick = { topBarClick(ClickNavigation.PopBackstack) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSearch(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },

        navigationIcon = {
            IconButton(onClick = { topBarClick(ClickNavigation.PopBackstack) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}

@Composable
fun Contacts(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    topBarClick: (ClickNavigation) -> Unit,
) {

    val contacts = contactsViewModel.contacts.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        fetchUserId {
            if (it.userId != null) {
                // contactsViewModel.fetchContacts(loggedInUserId)
                contactsViewModel.fetchReceivedInvites(it.userId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarContacts(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            when (contacts) {
                is ContactsViewModel.UserContacts.Error -> {
                    println("Shared ***** ERROR")
                }

                ContactsViewModel.UserContacts.Pending -> {
                    PendingCard(dimensionResource(id = R.dimen.card_pending_spinner))
                }

                is ContactsViewModel.UserContacts.Success -> {
                    contacts.data.forEach { contact ->

                        when (contact) {
                            is ReceivedContactInvite -> {
                                ContactCard(
                                    firstLine = contact.userName,
                                    secondLine = contact.name,
                                    thirdLine = "date here",
                                    hasButtonPair = true,
                                    primaryButtonText = stringResource(id = R.string.connect_cancel),
                                    secondaryButtonText = stringResource(id = R.string.connect_to_user),
                                    avatar = R.drawable.ic_dashboard_black_24dp,
                                    onClick = { event ->
                                        if (event == ContactCardEvent.ConnectClick) {
                                            fetchUserId { loggedInUser ->
                                                // TODO: store userId in cache
                                                if (loggedInUser.userId != null) {
                                                    contactsViewModel.contactButtonClickAction(
                                                        ContactCardEvent.Connect(
                                                            ConnectionAccepted(
                                                                userName = contact.userName,
                                                                name = contact.name,
                                                                avatarUri = contact.avatarUri,
                                                                rowId = contact.rowId,
                                                                senderUserId = contact.userId,
                                                                receiverUserId = loggedInUser.userId,
                                                            )
                                                        )
                                                        /*ContactCardEvent.Connect(
                                                            contact.userId,
                                                            loggedInUser.userId,
                                                        )*/
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }

                            is SentContactInvite -> {
                                ContactCard(
                                    firstLine = contact.userName,
                                    secondLine = contact.name,
                                    thirdLine = "date here",
                                    hasButtonPair = false,
                                    primaryButtonText = stringResource(id = R.string.connect_cancel),
                                    secondaryButtonText = stringResource(id = R.string.connect_to_user),
                                    avatar = R.drawable.ic_dashboard_black_24dp,
                                    onClick = { event ->

                                    },
                                )
                            }

                            is CurrentContact -> {
                                ContactCard(
                                    firstLine = contact.userName,
                                    secondLine = contact.name,
                                    thirdLine = "date here",
                                    hasButtonPair = false,
                                    primaryButtonText = stringResource(id = R.string.connect_cancel),
                                    secondaryButtonText = stringResource(id = R.string.connect_to_user),
                                    avatar = R.drawable.ic_dashboard_black_24dp,
                                    onClick = { event ->

                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Groups(mainViewModel: MainViewModel = hiltViewModel(), topBarClick: (ClickNavigation) -> Unit) {
    Scaffold(
        topBar = {
            TopBarGroups(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Text(text = "Groups")
        }
    }
}

@Composable
fun Search(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    topBarClick: (ClickNavigation) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBarSearch(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                }
            )
        },
    ) { innerPadding ->

        val searchQuery = contactsViewModel.searchResults.collectAsStateWithLifecycle().value
        val hasConnection = contactsViewModel.hasConnection.collectAsStateWithLifecycle().value
        val hasPendingInvite = contactsViewModel.hasPendingInvite.collectAsStateWithLifecycle().value
        val isSendingInvite = contactsViewModel.isSendingInvite.collectAsStateWithLifecycle().value
        val isCancellingInvite =
            contactsViewModel.isCancellingInvite.collectAsStateWithLifecycle().value
        val receiverQueryPending =
            contactsViewModel.receiverQueryPending.collectAsStateWithLifecycle().value

        var input by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            TextField(
                value = input,
                maxLines = 2,
                onValueChange = {
                    input = it
                    contactsViewModel.onQueryChange(it)
                },
                label = { Text(stringResource(id = R.string.user_search)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
            )

            when (searchQuery) {
                NetResult.Pending -> {
                    // TODO: maybe remove this
                    //  Pending()
                }

                is NetResult.Success -> {
                    val user = (searchQuery as? NetResult.Success)?.data
                    UserCard(
                        user,
                        hasConnection = hasConnection,
                        hasPendingInvite = hasPendingInvite,
                        receiverQueryPending = receiverQueryPending,
                        isSendingInvite = isSendingInvite,
                        isCancellingInvite = isCancellingInvite,
                        connectionEvent = { connectionEvent ->
                            contactsViewModel.userConnectionEvent(connectionEvent)
                        },
                        onButtonClick = { receiverUserId ->
                           // contactsViewModel.createConnectInvite(receiverUserId)
                        })
                }

                is NetResult.Error -> {

                }
            }

        }
    }
}

@Composable
fun Pending() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun PendingCard(spinnerSize: Dp) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(spinnerSize),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun Error() {
    Column {

    }
}

@Composable
fun UserCard(
    user: User?,
    hasConnection: Boolean,
    hasPendingInvite: Boolean,
    isSendingInvite: Boolean,
    isCancellingInvite: Boolean,
    receiverQueryPending: Boolean,
    connectionEvent: (ConnectionEvent) -> Unit,
    onButtonClick: (String?) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.card_padding)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (receiverQueryPending) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PendingCard(16.dp)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = R.drawable.ic_dashboard_black_24dp,// "https://example.com/image.jpg",
                        // TODO: add an image user.avatarUri
                        contentDescription = stringResource(id = R.string.user_avatar),
                        modifier = Modifier
                            .width(60.dp)
                            .padding(
                                dimensionResource(id = R.dimen.card_padding_start),
                                dimensionResource(id = R.dimen.card_padding_top),
                            )
                    )
                    Column(
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.card_padding_start),
                                dimensionResource(id = R.dimen.card_padding_top),
                            )
                            .weight(1f)
                    ) {
                        user?.firstName?.let { Text(it) }
                        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_height)))
                        user?.name?.let { Text(it) }
                    }
                }

                if (hasConnection) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(2.dp),
                    ) {
                        Text("Connected")
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_width)))
                        OutlinedButton(
                            onClick = {
                                onButtonClick(user?.userName) // TODO: moving this
                                connectionEvent(ConnectionEvent.DisconnectEvent)
                            },
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text("Disconnect")
                        }
                    }
                } else {
                    if (hasPendingInvite) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp),
                        ) {
                            Text("Pending")
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_width)))
                            OutlinedButton(
                                onClick = {
                                    user?.userId?.let { receiverUserId ->
                                        connectionEvent(
                                            ConnectionEvent.CancelEvent(
                                                user.userId, // TODO: receiver id
                                                receiverUserId,
                                            )
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(4.dp),
                                enabled = !isCancellingInvite,
                            ) {
                                Text(stringResource(id = R.string.connect_cancel))
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                user?.userId?.let { receiverUserId ->
                                    connectionEvent(ConnectionEvent.ConnectEvent(receiverUserId))
                                }
                            },
                            shape = RoundedCornerShape(4.dp),
                            enabled = !isSendingInvite
                        ) {
                            Text(stringResource(id = R.string.connect_to_user))
                        }
                    }
                }
            }
        }
    }
}

// TODO: remove just to test
fun testUser1(userId: String): User {
    return User.builder()
        .userId(userId)
        .firstName("Larry")
        .lastName("McCarty")
        .userName("LarryM")
        //.userId(userId)
        .email("lwmccarty@gmail.com")
        .phone("+14808104545")
        .name("LM")
        .avatarUri("https://www.google.com")
        .build()
}

fun testUser2(userId: String): User {
    return User.builder()
        .userId(userId)
        .firstName("Lebron")
        .lastName("James")
        .userName("KingJames")
        //.userId(userId)
        .email("lmccarty@outlook.com")
        .phone("+14805554545")
        .name("Bron")
        .avatarUri("https://example.com/avatar.png")
        .build()


        /*.firstName("Lebron")
        .lastName("James")
        .name("Lebron J")
        .phone("+14805554545")
        //.userName("KingJames")
        .email("lmccarty@outlook.com")
        .avatarUri("https://example.com/avatar.png")
        //.userId(userId)
        .id(userId)
        .build()*/
}

data class Ids(
    val rowId: String,
    val userId: String,
)

data class ConnectionAccepted(
    val userName: String,
    val name: String?,
    val avatarUri: String,
    val rowId: String,
    val senderUserId: String,
    val receiverUserId: String,
)