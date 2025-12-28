package com.cerqa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cerqa.models.*
import com.cerqa.viewmodels.ContactCardConnectionEvent
import com.cerqa.viewmodels.ContactCardEvent
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.viewmodels.MessageType
import com.cerqa.viewmodels.SearchViewModel

@Composable
fun Contacts(
    searchQuery: String,
    searchViewModel: SearchViewModel,
    contactsViewModel: ContactsViewModel,
    mainViewModel: MainViewModel,
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val contactsUiState by contactsViewModel.uiState.collectAsState()
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<SearchUser?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogData by remember { mutableStateOf<ConfirmDialogData?>(null) }
    var showInviteSentSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contactsViewModel.fetchAllContacts()
        searchViewModel.loadDeviceContacts()
    }

    LaunchedEffect(uiState.lastSentInvite) {
        uiState.lastSentInvite?.let { inviteData ->
            println("Contacts: Detected new sent invite, adding to contacts list")
            contactsViewModel.addSentInvite(
                inviteId = inviteData.inviteId,
                receiverUserId = inviteData.receiverUserId,
                receiverUserName = inviteData.receiverUserName,
                receiverName = inviteData.receiverName,
                senderUserId = inviteData.senderUserId
            )
        }
    }

    LaunchedEffect(uiState.idle) {
        if (uiState.idle && searchQuery.isEmpty()) {
            contactsViewModel.fetchAllContacts()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (uiState.pending || contactsUiState.pending) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        when (uiState.message) {
            MessageType.SUCCESS -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "User added to backend successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // TODO: hide
            MessageType.INVITE_SENT -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Invite sent successfully!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            MessageType.ERROR -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "An error occurred",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            else -> {} // TODO:  what else?
        }
        if (
            uiState.idle && contactsUiState.contacts.isEmpty() &&
            uiState.nonAppUsers.isEmpty() &&
            !contactsUiState.pending && !uiState.pending
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Search for users to connect",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (
            uiState.idle && searchQuery.isEmpty() &&
            !contactsUiState.pending &&
            !uiState.pending &&
            (contactsUiState.contacts.isNotEmpty() ||
                    uiState.nonAppUsers.isNotEmpty())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contactsUiState.contacts, key = { it.contactId }) { contact ->
                    when (contact) {
                        is ReceivedContactInvite -> {
                            ReceivedInviteCard(
                                contact = contact,
                                onAccept = {
                                    confirmDialogData = ConfirmDialogData(
                                        title = "Accept Invite",
                                        message = "Accept connection invite from ${contact.userName ?: "this user"}?",
                                        onConfirm = {
                                            contactsViewModel.userConnectionEvent(
                                                0,
                                                ContactCardEvent.AcceptConnection(contact.userId)
                                            )
                                        }
                                    )
                                    showConfirmDialog = true
                                },
                                onDeny = {
                                    confirmDialogData = ConfirmDialogData(
                                        title = "Deny Invite",
                                        message = "Deny connection invite from ${contact.userName ?: "this user"}?",
                                        onConfirm = {
                                            contactsViewModel.userConnectionEvent(
                                                0,
                                                ContactCardEvent.DeleteReceivedInvite(contact.userId)
                                            )
                                        }
                                    )
                                    showConfirmDialog = true
                                }
                            )
                        }

                        is SentInviteContactInvite -> { // TODO: change. name
                            SentInviteCard(
                                contact = contact,
                                onCancel = {
                                    confirmDialogData = ConfirmDialogData(
                                        title = "Cancel Invite",
                                        message = "Cancel connection invite to ${contact.userName ?: "this user"}?",
                                        onConfirm = {
                                            contactsViewModel.userConnectionEvent(
                                                0,
                                                ContactCardEvent.CancelSentInvite(contact.userId)
                                            )
                                        }
                                    )
                                    showConfirmDialog = true
                                }
                            )
                        }

                        is CurrentContact -> {
                            CurrentContactCard(
                                contact = contact,
                                onDelete = {
                                    confirmDialogData = ConfirmDialogData(
                                        title = "Delete Contact",
                                        message = "Delete ${contact.userName ?: "this user"} from your contacts?",
                                        onConfirm = {
                                            contactsViewModel.userConnectionEvent(
                                                0,
                                                ContactCardEvent.DeleteContact(contact.contactId)
                                            )
                                        }
                                    )
                                    showConfirmDialog = true
                                }
                            )
                        }
                    }
                }

                // Device contacts section
                if (uiState.nonAppUsers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Device Contacts",// TODO: use string resource
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(uiState.nonAppUsers, key = { it.phoneNumbers.firstOrNull() ?: it.name }) { contact ->
                        DeviceContactCard(
                            contact = contact,
                            onInviteClick = { phoneNumber ->
                                searchViewModel.inviteSentEvent(
                                    ContactCardConnectionEvent.InvitePhoneNumberConnectEvent(phoneNumber)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Search results, show when user is searching
        if (!uiState.idle && searchQuery.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search results header
                if (uiState.results.isNotEmpty()) {
                    item {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // User search results
                items(uiState.results, key = { it.id }) { user ->
                    SearchUserCard(
                        user = user,
                        onConnectClick = {
                            selectedUser = user
                            showInviteDialog = true
                        },
                        onCancelInvite = { userId ->
                            confirmDialogData = ConfirmDialogData(
                                title = "Cancel Invite",
                                message = "Cancel your connection invite to ${user.userName ?: "this user"}?",
                                onConfirm = {
                                    searchViewModel.cancelInvite(userId)
                                }
                            )
                            showConfirmDialog = true
                        }
                    )
                }

                // Empty state for search results
                if (uiState.results.isEmpty() && !uiState.pending) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No users found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Invite confirmation dialog
    if (showInviteDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Send Connection Invite") },
            text = {
                Text("Send a connection invite to ${selectedUser?.userName ?: "this user"}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedUser?.let { user ->
                            searchViewModel.inviteSentEvent(
                                ContactCardConnectionEvent.InviteConnectEvent(
                                    receiverUserId = user.userId,
                                    rowId = user.id
                                )
                            )
                        }
                        showInviteDialog = false
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for accept/deny/cancel/delete actions
    if (showConfirmDialog && confirmDialogData != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(confirmDialogData!!.title) }, // TODO: no !!
            text = { Text(confirmDialogData!!.message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDialogData!!.onConfirm() // TODO: don't use !!
                        showConfirmDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class ConfirmDialogData(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit
)

@Composable
fun ReceivedInviteCard(
    contact: ReceivedContactInvite,
    onAccept: () -> Unit,
    onDeny: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.userName ?: "Unknown User",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Wants to connect",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                contact.phoneNumber?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDeny,
                modifier = Modifier.weight(1f)
            ) {
                Text("Deny")
            }
            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f)
            ) {
                Text("Accept")
            }
        }
    }
}

@Composable
fun SentInviteCard(
    contact: SentInviteContactInvite,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.userName ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Invite sent - waiting for response",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            contact.phoneNumber?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        OutlinedButton(onClick = onCancel) {
            Text("Cancel")
        }
    }
}

@Composable
fun CurrentContactCard(
    contact: CurrentContact,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.userName ?: contact.name ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Connected",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            contact.phoneNumber?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}

@Composable
fun SearchUserCard(
    user: SearchUser,
    onConnectClick: () -> Unit,
    onCancelInvite: ((String) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.userName ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium
            )
            user.phone?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Connection status or action button
        when (user.contactType) {
            ContactType.RECEIVED -> {
                Button(
                    onClick = { /* Disabled */ },
                    enabled = false
                ) {
                    Text("Invite Received")
                }
            }

            ContactType.SENT -> {
                OutlinedButton(
                    onClick = { onCancelInvite?.invoke(user.userId) }
                ) {
                    Text("Cancel Invite")
                }
            }

            ContactType.CURRENT -> {
                Button(
                    onClick = { /* Disabled */ },
                    enabled = false
                ) {
                    Text("Connected")
                }
            }

            null -> {
                Button(
                    onClick = onConnectClick,
                    enabled = user.connectButtonEnabled
                ) {
                    Text("Connect")
                }
            }
        }
    }
}

@Composable
fun DeviceContactCard(
    contact: DeviceContact,
    onInviteClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium
            )
            contact.phoneNumbers.firstOrNull()?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "From device contacts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = {
                contact.phoneNumbers.firstOrNull()?.let { phone ->
                    onInviteClick(phone)
                }
            }
        ) {
            Text("Invite")
        }
    }
}