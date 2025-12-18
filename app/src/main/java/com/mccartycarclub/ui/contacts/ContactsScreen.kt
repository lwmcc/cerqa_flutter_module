package com.mccartycarclub.ui.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mccartycarclub.R
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.ContactType
import com.mccartycarclub.repository.CurrentContact
import com.mccartycarclub.repository.ReceivedContactInvite
import com.mccartycarclub.repository.SentInviteContactInvite
import com.mccartycarclub.ui.components.AlertDialogData
import com.mccartycarclub.ui.components.AnimatedLoadingSpinner
import com.mccartycarclub.ui.components.CardListButton
import com.mccartycarclub.ui.components.ConfirmationDialog
import com.mccartycarclub.ui.components.ContactCardConnectionEvent
import com.mccartycarclub.ui.components.ContactCardEvent
import com.mccartycarclub.ui.components.CurrentContactCard
import com.mccartycarclub.ui.components.ListSection
import com.mccartycarclub.ui.components.NoDataFound
import com.mccartycarclub.ui.components.ReceivedInviteContactCard
import com.mccartycarclub.ui.components.SentContactCard
import com.mccartycarclub.viewmodels.ContactsViewModel
import com.mccartycarclub.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun ContactsScreen(
    paddingValues: PaddingValues,
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    topBarClick: (ClickNavigation) -> Unit,
) {
    val contacts = contactsViewModel.uiState
    val userId by contactsViewModel.userId.collectAsStateWithLifecycle()
    val searchUiState = searchViewModel.uiState

    var openAlertDialog by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    var connectionEvent by remember { mutableStateOf<ContactCardEvent?>(null) }

    var listIndex by remember { mutableIntStateOf(0) }
    var alertDialogData by remember { mutableStateOf<AlertDialogData?>(null) }

    // Search state
    var query by remember { mutableStateOf("") }
    var clearSearchVisible by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    LaunchedEffect(userId) {
        contactsViewModel.fetchAllContacts(userId)
    }

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

    val displayContacts = if (isSearching && query.isNotEmpty()) {
        emptyList() // We'll show search results separately
    } else {
        contacts.contacts
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        topBar = {
            OutlinedTextField(
                value = query,
                onValueChange = { newValue ->
                    query = newValue
                    clearSearchVisible = newValue.isNotEmpty()
                    isSearching = newValue.isNotEmpty()

                    if (newValue.isNotEmpty()) {
                        searchViewModel.onQueryChange(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text("Search users...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (clearSearchVisible) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.clickable {
                                query = ""
                                clearSearchVisible = false
                                isSearching = false
                                searchViewModel.onQueryChange("")
                            }
                        )
                    } else {
                        IconButton(onClick = { topBarClick(ClickNavigation.NavToSearch) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Contact")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Search is triggered automatically via onValueChange
                    }
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            AnimatedLoadingSpinner(
                density = density,
                dataPending = if (isSearching) searchUiState.pending else contacts.pending,
                spinnerSize = R.dimen.card_pending_spinner,
                slideIn = (-20).dp,
            )

            when {
                isSearching && query.isNotEmpty() -> {
                    if (searchUiState.results.isEmpty() && !searchUiState.pending && !searchUiState.idle) {
                        NoDataFound(message = stringResource(R.string.user_search))
                    } else if (!searchUiState.idle) {
                        LazyColumn(modifier = Modifier.testTag("searchResultsTag")) {
                            items(searchUiState.results.size) { index ->
                                val item = searchUiState.results[index]
                                ListSection(
                                    image = R.drawable.ic_dashboard_black_24dp,
                                    contentDescription = stringResource(id = R.string.user_avatar),
                                    title = item.userName.toString(),
                                    width = 60.dp,
                                    content = {
                                        when (item.contactType) {
                                            ContactType.RECEIVED -> {
                                                Text(
                                                    text = stringResource(R.string.connect_invite_received),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            ContactType.SENT -> {
                                                Text(
                                                    text = stringResource(R.string.connect_invite_sent),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            ContactType.CURRENT -> {
                                                Text(
                                                    text = stringResource(R.string.connected),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }

                                            null -> {
                                                CardListButton(
                                                    text = stringResource(id = R.string.connect_to_user),
                                                    isEnabled = item.connectButtonEnabled,
                                                    onClick = {
                                                        // Send invite to connect
                                                        val event: ContactCardConnectionEvent =
                                                            ContactCardConnectionEvent.InviteConnectEvent(
                                                                receiverUserId = item.userId,
                                                                rowId = item.id
                                                            )
                                                        searchViewModel.inviteSentEvent(event)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                contacts.message != null -> {
                    println("SharedComposables ***** CONTACTS MESSAGE ${contacts.message}")
                }

                displayContacts.isEmpty() && searchUiState.nonAppUsers.isEmpty() && !contacts.pending -> {
                    NoDataFound(message = stringResource(id = R.string.connect_invite_users))
                }

                else -> {
                    LazyColumn(modifier = Modifier.testTag("contactsTag")) {
                        displayContacts.forEachIndexed { index, contact ->
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
                                                    description =
                                                        R.string.dialog_remove_invite_to_connect_description,
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
                                                    description =
                                                        R.string.dialog_accept_invite_to_connect_description,
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
                                                    description =
                                                        R.string.dialog_cancel_invite_to_connect_description,
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
                                            contact = contact.name,
                                            avatar = R.drawable.baseline_person_24,
                                        )
                                    }
                                }
                            }
                        }

                        // Device contacts section
                        if (searchUiState.nonAppUsers.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.device_contacts_header),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                                )
                            }

                            items(searchUiState.nonAppUsers.size) { index ->
                                val contact = searchUiState.nonAppUsers[index]
                                ListSection(
                                    image = R.drawable.ic_dashboard_black_24dp,
                                    contentDescription = stringResource(id = R.string.user_avatar),
                                    title = contact.name,
                                    width = 60.dp,
                                    content = {
                                        CardListButton(
                                            text = stringResource(id = R.string.invite_user),
                                            isEnabled = true,
                                            onClick = {
                                                contact.phoneNumbers.firstOrNull()?.let { phoneNumber ->
                                                    searchViewModel.inviteSentEvent(
                                                        ContactCardConnectionEvent.InvitePhoneNumberConnectEvent(phoneNumber)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}