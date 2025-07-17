package com.mccartycarclub.ui.contacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.mccartycarclub.repository.CurrentContact
import com.mccartycarclub.repository.ReceivedContactInvite
import com.mccartycarclub.repository.SentInviteContactInvite
import com.mccartycarclub.ui.components.AlertDialogData
import com.mccartycarclub.ui.components.AnimatedLoadingSpinner
import com.mccartycarclub.ui.components.ConfirmationDialog
import com.mccartycarclub.ui.components.ContactCardEvent
import com.mccartycarclub.ui.components.CurrentContactCard
import com.mccartycarclub.ui.components.NoDataFound
import com.mccartycarclub.ui.components.ReceivedInviteContactCard
import com.mccartycarclub.ui.components.SentContactCard
import com.mccartycarclub.ui.viewmodels.ContactsViewModel

@Suppress("LongMethod")
@Composable
fun ContactsScreen(
    paddingValues: PaddingValues,
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    topBarClick: (ClickNavigation) -> Unit,
) {
    val contacts = contactsViewModel.uiState
    val userId by contactsViewModel.userId.collectAsStateWithLifecycle()

    var openAlertDialog by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    var connectionEvent by remember { mutableStateOf<ContactCardEvent?>(null) }

    var listIndex by remember { mutableIntStateOf(0) }
    var alertDialogData by remember { mutableStateOf<AlertDialogData?>(null) }

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

    Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            AnimatedLoadingSpinner(
                density = density,
                dataPending = contacts.pending,
                spinnerSize = R.dimen.card_pending_spinner,
                slideIn = (-20).dp,
            )

            when {
                contacts.message != null -> {
                    println("SharedComposables ***** CONTACTS MESSAGE ${contacts.message}")
                }

                contacts.contacts.isEmpty() && !contacts.pending -> {
                    NoDataFound(message = stringResource(id = R.string.connect_invite_users))
                }

                else -> {
                    LazyColumn(modifier = Modifier.testTag("contactsTag")) {
                        contacts.contacts.forEachIndexed { index, contact ->
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
                    }
                }
            }
        }
    }
}