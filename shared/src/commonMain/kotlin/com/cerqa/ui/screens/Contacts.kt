package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cerqa.models.*
import com.cerqa.ui.components.ChatBottomSheet
import com.cerqa.viewmodels.ContactCardConnectionEvent
import com.cerqa.viewmodels.ContactCardEvent
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.viewmodels.MessageType
import com.cerqa.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contacts(
    searchQuery: String,
    searchViewModel: SearchViewModel,
    contactsViewModel: ContactsViewModel,
    mainViewModel: MainViewModel,
    onNavigateToConversation: (contactId: String, userName: String) -> Unit = { _, _ -> },
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val contactsUiState by contactsViewModel.uiState.collectAsState()
    var showInviteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<SearchUser?>(null) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogData by remember { mutableStateOf<ConfirmDialogData?>(null) }
    var selectedContactForBottomSheet by remember { mutableStateOf<CurrentContact?>(null) }

    LaunchedEffect(Unit) {
        contactsViewModel.fetchAllContacts()
        searchViewModel.loadDeviceContacts()
    }

    LaunchedEffect(uiState.lastSentInvite) {
        uiState.lastSentInvite?.let { inviteData ->
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

    // Bottom sheet for current contact options
    if (selectedContactForBottomSheet != null) {
        ChatBottomSheet(
            showBottomSheet = { show ->
                if (!show) selectedContactForBottomSheet = null
            },
            content = {
                Text(
                    text = selectedContactForBottomSheet?.userName ?: "Contact Options",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedButton(
                    onClick = {
                        selectedContactForBottomSheet?.let { contact ->
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
                        selectedContactForBottomSheet = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete Contact")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        )
    }

    // Invite dialog
    if (showInviteDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Send Connection Invite") },
            text = { Text("Send a connection invite to ${selectedUser?.userName ?: "this user"}?") },
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

    // Confirmation dialog
    if (showConfirmDialog && confirmDialogData != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(confirmDialogData!!.title) },
            text = { Text(confirmDialogData!!.message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDialogData!!.onConfirm()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.pending || contactsUiState.pending -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.idle && contactsUiState.contacts.isEmpty() &&
                    uiState.nonAppUsers.isEmpty() && searchQuery.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search for users to connect",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Status messages
                    when (uiState.message) {
                        MessageType.SUCCESS -> {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Text(
                                        text = "User added successfully!",
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        MessageType.INVITE_SENT -> {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        }

                        MessageType.ERROR -> {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        }

                        else -> {}
                    }

                    // Search results (when actively searching)
                    if (!uiState.idle && searchQuery.isNotEmpty()) {
                        if (uiState.results.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Search Results",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                )
                            }
                        }

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

                        if (uiState.results.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
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
                    } else {
                        // My Contacts (when not searching)
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

                                is SentInviteContactInvite -> {
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
                                        onClick = {
                                            onNavigateToConversation(
                                                contact.userId,
                                                contact.userName ?: contact.name ?: "Unknown"
                                            )
                                        },
                                        onLongClick = {
                                            selectedContactForBottomSheet = contact
                                        }
                                    )
                                }
                            }
                        }

                        // Device contacts section
                        if (uiState.nonAppUsers.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Device Contacts",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp, )
                                )
                            }

                            items(
                                uiState.nonAppUsers,
                                key = { it.phoneNumbers.firstOrNull() ?: it.name }
                            ) { contact ->
                                DeviceContactCard(
                                    contact = contact,
                                    onInviteClick = { phoneNumber ->
                                        searchViewModel.inviteSentEvent(
                                            ContactCardConnectionEvent.InvitePhoneNumberConnectEvent(
                                                phoneNumber
                                            )
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

data class ConfirmDialogData(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit
)

@Composable
private fun ReceivedInviteCard(
    contact: ReceivedContactInvite,
    onAccept: () -> Unit,
    onDeny: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = contact.userName ?: contact.name ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = "Wants to connect",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onDeny) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Deny",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(onClick = onAccept) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

@Composable
private fun SentInviteCard(
    contact: SentInviteContactInvite,
    onCancel: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = contact.userName ?: contact.name ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = "Invite sent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingContent = {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Cancel invite",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
private fun CurrentContactCard(
    contact: CurrentContact,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = contact.userName ?: contact.name ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            contact.phoneNumber?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}

@Composable
private fun SearchUserCard(
    user: SearchUser,
    onConnectClick: () -> Unit,
    onCancelInvite: ((String) -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Text(
                text = user.userName ?: "Unknown User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            user.phone?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        },
        trailingContent = {
            when (user.contactType) {
                ContactType.RECEIVED -> {
                    Text(
                        text = "Invite received",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                ContactType.SENT -> {
                    IconButton(
                        onClick = { onCancelInvite?.invoke(user.userId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel invite",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ContactType.CURRENT -> {
                    Text(
                        text = "Connected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ContactType.NONE -> {
                    IconButton(onClick = onConnectClick) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send invite",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                null -> TODO()
            }
        }
    )
}

@Composable
private fun DeviceContactCard(
    contact: DeviceContact,
    onInviteClick: (String) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            contact.phoneNumbers.firstOrNull()?.let { phone ->
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact avatar",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            contact.phoneNumbers.firstOrNull()?.let { phoneNumber ->
                TextButton(onClick = { onInviteClick(phoneNumber) }) {
                    Text("Invite")
                }
            }
        }
    )
}