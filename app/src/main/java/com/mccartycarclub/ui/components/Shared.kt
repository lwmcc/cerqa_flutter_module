package com.mccartycarclub.ui.components

import android.util.Log
import androidx.annotation.DimenRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
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
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.MainActivity.Companion.CONTACTS_SCREEN
import com.mccartycarclub.MainActivity.Companion.GROUPS_SCREEN
import com.mccartycarclub.MainActivity.Companion.MAIN_SCREEN
import com.mccartycarclub.MainActivity.Companion.NOTIFICATIONS_SCREEN
import com.mccartycarclub.MainActivity.Companion.SEARCH_SCREEN
import com.mccartycarclub.R
import com.mccartycarclub.domain.model.ConnectedSearch
import com.mccartycarclub.domain.model.ReceivedInviteFromUser
import com.mccartycarclub.domain.model.SentInviteToUser
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.navigation.AppNavigationActions
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.CurrentContact
import com.mccartycarclub.repository.ReceivedContactInvite
import com.mccartycarclub.repository.SentInviteContactInvite
import com.mccartycarclub.repository.UiStateResult
import com.mccartycarclub.ui.viewmodels.ContactsViewModel
import com.mccartycarclub.ui.viewmodels.MainViewModel

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

        composable(NOTIFICATIONS_SCREEN) { backStackEntry ->
            Notifications(topBarClick = {
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

        ClickNavigation.NavToNotifications -> {
            navActions.navigateToNotifications()
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

                                // TODO: testing
                               // mainViewModel.setLocalUserId(userId!!)

                                Amplify.API.mutate(
                                    ModelMutation.create(testUser1(userId!!)),
                                    { response -> // TODO: response?
                                    // This is were userId is added to prefs
                                        mainViewModel.setLocalUserId(userId)
                                    },
                                    { error ->
                                        Log.e("MainActivity *****", "User creation failed", error)
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

            Box(modifier = Modifier.clickable {
                topBarClick(ClickNavigation.NavToNotifications)
            }) {
                BadgedBox(
                    badge = {
                        Badge()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Email",
                    )
                }
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

    val contacts by contactsViewModel.contactsState.collectAsStateWithLifecycle()
    val dataPending by contactsViewModel.dataPending.collectAsStateWithLifecycle()
    val userId by contactsViewModel.userId.collectAsStateWithLifecycle()
    val allContacts = contactsViewModel.contacts
    var openAlertDialog by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    var connectionEvent by remember { mutableStateOf<ContactCardEvent?>(null) }
    var listIndex by remember { mutableIntStateOf(0) }
    var alertDialogData by remember { mutableStateOf<AlertDialogData?>(null) }
    val density = LocalDensity.current

    when {
        openAlertDialog -> {
            ConfirmationDialog(
                alertDialogData = alertDialogData,
                onDismissRequest = {
                    openAlertDialog = false
                },
                onConfirmation = {
                    openAlertDialog = false
                    connectionEvent?.let { event ->
                        contactsViewModel.userConnectionEvent(listIndex, event)
                    }
                },
            )
        }
    }

    contactsViewModel.fetchReceivedInvites(userId)

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

            AnimatedLoadingSpinner(
                density = density,
                dataPending = dataPending,
                spinnerSize = R.dimen.card_pending_spinner,
                slideIn = (-20).dp,
            )

            when (contacts) {
                is ContactsViewModel.UserContacts.NoInternet -> {
                    println("Shared ***** NO INTERNET")
                }

                is ContactsViewModel.UserContacts.Error -> {
                    println("Shared ***** ERROR")
                }

                is ContactsViewModel.UserContacts.Success -> {
                    if (allContacts.isEmpty()) {
                        NoDataFound(message = stringResource(id = R.string.connect_invite_users))
                    } else {
                        LazyColumn {
                            allContacts.forEachIndexed { index, contact ->
                                when (contact) {
                                    is ReceivedContactInvite -> {
                                        item {
                                            ReceivedInviteContactCard(
                                                contact = contact,
                                                primaryButtonText = stringResource(id = R.string.connect_remove),
                                                secondaryButtonText = stringResource(id = R.string.connect_to_user),
                                                avatar = R.drawable.ic_dashboard_black_24dp,
                                                onDismissClick = { event ->
                                                    listIndex = index
                                                    connectionEvent = event
                                                    openAlertDialog = true
                                                    alertDialogData = AlertDialogData(
                                                        icon = R.drawable.baseline_person_off_24,
                                                        title = R.string.dialog_remove_invite_to_connect_title,
                                                        description = R.string.dialog_remove_invite_to_connect_description,
                                                        dialogIconDescription = R.string.dialog_icon_description,
                                                        dismiss = R.string.dialog_button_dismiss,
                                                        confirm = R.string.connect_remove,
                                                    )
                                                },
                                                onConfirmClick = { event ->
                                                    listIndex = index
                                                    connectionEvent = event
                                                    openAlertDialog = true
                                                    alertDialogData = AlertDialogData(
                                                        icon = R.drawable.baseline_person_add_24,
                                                        title = R.string.dialog_accept_invite_to_connect_title,
                                                        description = R.string.dialog_accept_invite_to_connect_description,
                                                        dialogIconDescription = R.string.dialog_icon_description,
                                                        dismiss = R.string.dialog_button_dismiss,
                                                        confirm = R.string.dialog_button_accept,
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    is SentInviteContactInvite -> {
                                        item {
                                            SentContactCard(
                                                contact = contact,
                                                primaryButtonText = stringResource(id = R.string.connect_cancel),
                                                avatar = R.drawable.ic_dashboard_black_24dp,
                                                onClick = { event ->
                                                    selectedUserId = contact.userId
                                                    selectedContactId = contact.contactId
                                                    connectionEvent = event
                                                    openAlertDialog = true
                                                    alertDialogData = AlertDialogData(
                                                        icon = R.drawable.baseline_person_off_24,
                                                        title = R.string.dialog_cancel_invite_to_connect_title,
                                                        description = R.string.dialog_cancel_invite_to_connect_description,
                                                        dialogIconDescription = R.string.dialog_icon_description,
                                                        dismiss = R.string.dialog_button_dismiss,
                                                        confirm = R.string.connect_cancel,
                                                    )
                                                },
                                            )
                                        }
                                    }

                                    is CurrentContact -> {
                                        item {
                                            CurrentContactCard(
                                                contact = contact,
                                                primaryButtonText = stringResource(id = R.string.connect_remove),
                                                avatar = R.drawable.ic_dashboard_black_24dp,
                                                onClick = { event ->
                                                    connectionEvent = event
                                                    openAlertDialog = true
                                                    alertDialogData = AlertDialogData(
                                                        icon = R.drawable.baseline_person_remove_24,
                                                        title = R.string.dialog_remove_connection_to_user_title,
                                                        description = R.string.dialog_remove_connection_to_user_description,
                                                        dialogIconDescription = R.string.dialog_icon_description,
                                                        dismiss = R.string.dialog_button_dismiss,
                                                        confirm = R.string.connect_remove,
                                                    )
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ContactsViewModel.UserContacts.Idle -> {
                    // No action needed
                }
            }
        }
    }
}

@Composable
fun Groups(topBarClick: (ClickNavigation) -> Unit) {
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

        val searchQuery2 = contactsViewModel.searchResults2.collectAsStateWithLifecycle().value
        val inviteSentSuccess =
            contactsViewModel.inviteSentSuccess.collectAsStateWithLifecycle().value
        val searchQuery = contactsViewModel.searchResults.collectAsStateWithLifecycle().value
        val hasConnection = contactsViewModel.hasConnection.collectAsStateWithLifecycle().value
        val hasPendingInvite =
            contactsViewModel.hasPendingInvite.collectAsStateWithLifecycle().value
        val isSendingInvite = contactsViewModel.isSendingInvite.collectAsStateWithLifecycle().value
        val isCancellingInvite =
            contactsViewModel.isCancellingInvite.collectAsStateWithLifecycle().value
        val receiverQueryPending =
            contactsViewModel.receiverQueryPending.collectAsStateWithLifecycle().value

        var input by remember { mutableStateOf("") }
        var openAlertDialog by remember { mutableStateOf(false) }
        var connectionEvent by remember { mutableStateOf<ContactCardEvent?>(null) }
        var alertDialogData by remember { mutableStateOf<AlertDialogData?>(null) }

        val density = LocalDensity.current

        when {
            openAlertDialog -> {
                ConfirmationDialog(
                    alertDialogData = alertDialogData,
                    onDismissRequest = {
                        openAlertDialog = false
                    },
                    onConfirmation = {
                        openAlertDialog = false
                        connectionEvent?.let { event ->
                            contactsViewModel.userConnectionEvent(connectionEvent = event)
                        }
                    },
                )
            }
        }

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

            when(searchQuery2) {
                is UiStateResult.Error -> {

                }

                UiStateResult.Idle -> {

                }

                UiStateResult.NoInternet -> {

                }

                UiStateResult.Pending -> {

                }

                is UiStateResult.Success -> {
                    val user2 = (searchQuery2 as? UiStateResult.Success)?.data
                    SearchResultUserCard(
                        user = user2,
                        isSendingInvite = isSendingInvite,
                        inviteSentSuccess = inviteSentSuccess,
                        connectionEvent = { event ->
                            openAlertDialog = true
                            connectionEvent = event
                            alertDialogData = AlertDialogData(
                                icon = R.drawable.sharp_contacts_24,
                                title = R.string.dialog_invite_to_connect_title,
                                description = R.string.dialog_invite_to_connect_description,
                                dialogIconDescription = R.string.dialog_icon_description,
                                dismiss = R.string.dialog_button_dismiss,
                                confirm = R.string.dialog_button_connect,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Notifications(topBarClick: (ClickNavigation) -> Unit) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Text(text = "Notifications")
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
fun AnimatedLoadingSpinner(
    density: Density,
    dataPending: Boolean,
    @DimenRes spinnerSize: Int,
    slideIn: Dp,
) {
    AnimatedVisibility(
        visible = dataPending,
        enter = slideInVertically {
            with(density) { slideIn.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Box {
            PendingCard(dimensionResource(id = spinnerSize))
        }
    }
}

@Composable
fun Error() {
    Column {

    }
}

@Composable
fun UserCard(
    // TODO: give this a better name, more descriptive
    user2: UserSearchResult?,
    user: User?,
    hasConnection: Boolean,
    hasPendingInvite: Boolean,
    isSendingInvite: Boolean,
    isCancellingInvite: Boolean,
    connectionEvent: (ContactCardEvent) -> Unit,
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

            if (hasConnection) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(2.dp),
                ) {
                    Text("Connected")
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_width)))
                    OutlinedButton(
                        onClick = {
                            connectionEvent(ContactCardEvent.DisconnectEvent)
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
                            user?.let { userId ->
                                connectionEvent(
                                    ContactCardEvent.InviteConnectEvent(userId.userId, userId.id)
                                )
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

@Composable
fun SearchResultUserCard(
    user: UserSearchResult?,
    isSendingInvite: Boolean,
    inviteSentSuccess: Boolean,
    connectionEvent: (ContactCardEvent) -> Unit,
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
                    user?.userName?.let { Text(it) }
                    when (user) {
                        is SentInviteToUser -> {
                            Column {
                                Text(user.userName)
                                Text("Sent Invite to User")
                            }
                        }

                        is ReceivedInviteFromUser -> {
                            Column {
                                Text(user.userName)
                                Text("Received Invite from User")
                                Button(
                                    onClick = { /*TODO*/ },
                                ) {
                                    Text("Accept")
                                }
                            }
                        }

                        is ConnectedSearch -> {
                            Column {
                                Text(user.userName)
                                Text("Connection")
                            }
                        }

                        else -> { // is UserSearchResult which needs to be last
                            if (!isSendingInvite) {
                                OutlinedButton(
                                    onClick = {
                                        user?.let { user ->
                                            connectionEvent(
                                                ContactCardEvent.InviteConnectEvent(
                                                    user.userId,
                                                    user.rowId
                                                )
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                ) {
                                    Text(stringResource(id = R.string.connect_to_user))
                                }
                            } else {
                                if (inviteSentSuccess) {
                                    Text("Invite Sent")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoDataFound(message: String) {
    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.card_padding))) {
        Text(text = message)
    }
}

@Composable
fun ConfirmationDialog(
    alertDialogData: AlertDialogData?,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    if (alertDialogData != null) {
        AlertDialog(
            icon = {
                Icon(
                    ImageVector.vectorResource(alertDialogData.icon),
                    contentDescription = stringResource(alertDialogData.dialogIconDescription)
                )
            },
            title = {
                Text(text = stringResource(id = alertDialogData.title))
            },
            text = {
                Text(text = stringResource(id = alertDialogData.description))
            },
            onDismissRequest = { onDismissRequest },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(id = alertDialogData.dismiss))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text(stringResource(id = alertDialogData.confirm))
                }
            },
        )
    }
}
/*
@Preview
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        dialogTitle = stringResource(id = R.string.dialog_delete_invite_title),
        dialogText = stringResource(id = R.string.dialog_delete_invite_description),
        dismissText = stringResource(id = R.string.dialog_button_dismiss),
        confirmText = stringResource(id = R.string.dialog_button_delete),
        icon = ImageVector.vectorResource(id = android.R.drawable.ic_dialog_alert),
        onDismissRequest = {

        },
        onConfirmation = {

        },
        alertDialogData = alertDialogData,
    )
}*/

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
        .email("lmccarty@outlook.com")
        .phone("+14805554545")
        .name("Bron")
        .avatarUri("https://example.com/avatar.png")
        .build()
}

fun testUser3(userId: String): User {
    return User.builder()
        .userId(userId)
        .firstName("Luka")
        .lastName("Doncic")
        .id(userId)
        .userName("Luka")
        .email("luka@gmail.com")
        .phone("+14805553211")
        .name("Luka")
        .avatarUri("https://example.com/luka/avatar.png")
        .build()
}

data class Ids(
    val rowId: String,
    val userId: String,
)

data class ConnectionAccepted(
    val userName: String,
    val name: String?,
    val avatarUri: String,
    val senderUserId: String,
    val userId: String,
    val createdAt: Temporal.DateTime?,
)

data class AlertDialogData(
    val icon: Int,
    val title: Int,
    val description: Int,
    val dialogIconDescription: Int,
    val dismiss: Int,
    val confirm: Int,
)