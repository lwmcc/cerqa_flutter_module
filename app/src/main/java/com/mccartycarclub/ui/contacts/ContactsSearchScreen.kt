package com.mccartycarclub.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mccartycarclub.R
import com.mccartycarclub.domain.model.SmsMessage
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.ContactType
import com.mccartycarclub.ui.components.AlertDialogData
import com.mccartycarclub.ui.components.AnimatedLoadingSpinner
import com.mccartycarclub.ui.components.BannerMessage
import com.mccartycarclub.ui.components.CardHeader
import com.mccartycarclub.ui.components.CardListButton
import com.mccartycarclub.ui.components.ConfirmationDialog
import com.mccartycarclub.ui.components.ContactCardConnectionEvent
import com.mccartycarclub.ui.components.ListSection
import com.mccartycarclub.ui.components.TopBarSearch
import com.mccartycarclub.viewmodels.ContactsViewModel
import com.mccartycarclub.viewmodels.SearchViewModel

@Suppress("LongMethod")
@Composable
fun ContactsSearchScreen(
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel,
    topBarClick: (ClickNavigation) -> Unit,
    sendSms: (SmsMessage) -> Unit,
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

        val uiState = searchViewModel.uiState
        val inviteSentSuccess =
            contactsViewModel.inviteSentSuccess.collectAsStateWithLifecycle().value
        val isSendingInvite = contactsViewModel.isSendingInvite.collectAsStateWithLifecycle().value

        var input by remember { mutableStateOf("") }
        var openAlertDialog by remember { mutableStateOf(false) }
        var connectionEvent by remember { mutableStateOf<ContactCardConnectionEvent?>(null) }
        var alertDialogData by remember { mutableStateOf<AlertDialogData?>(null) }
        var clearSearchVisible by remember { mutableStateOf(false) }

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
                            searchViewModel.inviteSentEvent(connectionEvent = event)
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

            when {
                uiState.message != null -> {
                    BannerMessage(
                        message = uiState.message,
                        onDismiss = {

                        },
                    )
                }

                uiState.idle -> {
                    println("Search ***** IDLE")
                }

                else -> {
                    println("Search ***** ELSE")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
            ) {

                if (clearSearchVisible) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.text_field_clear),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                input = ""
                                searchViewModel.onQueryChange("")
                            },
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.card_pending_spinner))
            )

            AnimatedLoadingSpinner(
                density = density,
                dataPending = uiState.pending,
                spinnerSize = R.dimen.card_pending_spinner,
                slideIn = (-20).dp,
            )

            LazyColumn {
                items(uiState.results, key = { it.id }) { item ->
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
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                ContactType.SENT -> {
                                    Text(
                                        text = stringResource(R.string.connect_invite_sent),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                ContactType.CURRENT -> {
                                    Text(
                                        text = stringResource(R.string.connected),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }

                                null -> {
                                    CardListButton(
                                        text = stringResource(id = R.string.connect_to_user),
                                        onClick = {
                                            openAlertDialog = true
                                            connectionEvent =
                                                ContactCardConnectionEvent.InviteConnectEvent(
                                                    receiverUserId = item.userId,
                                                    rowId = item.id,
                                                )
                                            alertDialogData = AlertDialogData(
                                                icon = R.drawable.sharp_contacts_24,
                                                title = R.string.dialog_invite_to_connect_title,
                                                description = R.string.dialog_invite_to_connect_description,
                                                dialogIconDescription = R.string.dialog_icon_description,
                                                dismiss = R.string.dialog_button_dismiss,
                                                confirm = R.string.dialog_button_connect,
                                            )
                                        },
                                        isEnabled = item.connectButtonEnabled,
                                    )
                                }
                            }
                        },
                    )
                }
            }

            LazyColumn {
                if (uiState.appUsers.isNotEmpty()) {
                    item {
                        CardHeader(
                            stringResource(R.string.connections_using_app),
                            R.dimen.card_padding_start,
                        )
                    }
                }
                items(uiState.appUsers) { user ->

                    ListSection(
                        image = R.drawable.ic_dashboard_black_24dp,
                        contentDescription = stringResource(id = R.string.user_avatar),
                        title = user.name,
                        width = 60.dp,
                        content = {
                            Text(
                                text = stringResource(R.string.user_search_connected),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                            )
                        }
                    )
                }

                if (uiState.nonAppUsers.isNotEmpty()) {
                    item {
                        CardHeader(
                            stringResource(R.string.connections_not_using_app),
                            R.dimen.card_padding_start,
                        )
                    }
                }

                items(uiState.nonAppUsers) { user ->
                    val title = stringResource(R.string.sms_title)
                    val appLink = stringResource(R.string.app_link)
                    ListSection(
                        image = R.drawable.ic_dashboard_black_24dp,
                        contentDescription = stringResource(id = R.string.user_avatar),
                        title = user.name,
                        width = 60.dp,
                        content = {
                            CardListButton(
                                text = stringResource(R.string.connect_invite_user),
                                onClick = {
                                    sendSms(
                                        SmsMessage(
                                            title = title,
                                            message = appLink,
                                            phoneNumber = user.phoneNumbers.first().toString(),
                                        )
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
