package com.mccartycarclub.ui.components

import androidx.annotation.DimenRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.R
import com.mccartycarclub.domain.model.ConnectedSearch
import com.mccartycarclub.domain.model.ReceivedInviteFromUser
import com.mccartycarclub.domain.model.SentInviteToUser
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.navigation.ClickNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = appBarTitle,
                style = MaterialTheme.typography.titleLarge,
            )
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToGroups)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_groups),
                    contentDescription = stringResource(R.string.connect_view_your_groups),
                )
            }
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToContacts)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_contacts),
                    contentDescription = stringResource(R.string.connect_view_your_contacts),
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
                        contentDescription = stringResource(R.string.connect_view_your_notifications),
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
            Text(
                text = appBarTitle,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToSearch)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_search),
                    contentDescription = stringResource(R.string.connect_content_description_search)
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
            Text(
                text = appBarTitle,
                style = MaterialTheme.typography.titleLarge,
            )
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
            Text(
                text = appBarTitle,
                style = MaterialTheme.typography.titleLarge,
            )
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
fun SearchResultUserCard(
    user: UserSearchResult,
    isSendingInvite: Boolean,
    inviteSentSuccess: Boolean,
    connectionEvent: (ContactCardConnectionEvent) -> Unit,
) {

    val showInviteSentSuccess by remember(inviteSentSuccess) { derivedStateOf { inviteSentSuccess } }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        dimensionResource(id = R.dimen.card_padding_start),
                        dimensionResource(id = R.dimen.card_padding_top),
                    )
                    .weight(1f)
            ) {
                SearchSection(
                    user = user,
                    isSendingInvite = isSendingInvite,
                    showInviteSentSuccess = showInviteSentSuccess,
                    connectionEvent = connectionEvent,
                )
            }
        }
    }
}

@Composable
fun NoDataFound(message: String) {
    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.card_padding))) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
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

@Composable
fun DescriptionAndButton(
    description: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    Column {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer, // TODO: add date
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 4.dp, vertical = 4.dp),
        )
        Button(
            onClick = {
                onClick()
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun SearchSection(
    user: UserSearchResult,
    isSendingInvite: Boolean,
    showInviteSentSuccess: Boolean,
    connectionEvent: (ContactCardConnectionEvent) -> Unit,
) {
    when (user) {
        is SentInviteToUser -> {
            ListSection(
                image = R.drawable.ic_dashboard_black_24dp,
                contentDescription = stringResource(id = R.string.user_avatar),
                title = user.userName,
                width = 60.dp,
                content = {
                    Text(
                        text = stringResource(id = R.string.connect_invite_sent),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer, // TODO: add date
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                    )
                }
            )
        }

        is ReceivedInviteFromUser -> {
            ListSection(
                image = R.drawable.ic_dashboard_black_24dp,
                contentDescription = stringResource(id = R.string.user_avatar),
                title = user.userName,
                width = 60.dp,
                content = {
                    DescriptionAndButton(
                        description = stringResource(id = R.string.connect_view_invite_received),
                        buttonText = stringResource(id = R.string.connect_button_accept),
                        onClick = {

                        })
                }
            )
        }

        is ConnectedSearch -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ListSection(
                    image = R.drawable.ic_dashboard_black_24dp,
                    contentDescription = stringResource(id = R.string.user_avatar),
                    title = user.userName,
                    width = 60.dp,
                    content = {
                        Text(
                            stringResource(id = R.string.connect_has_connection),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                )
            }
        }

        else -> { // Is UserSearchResult which needs to be last
            if (!isSendingInvite) { // Is not sending invite when VM initializes
                ListSection(
                    image = R.drawable.ic_dashboard_black_24dp,
                    contentDescription = stringResource(id = R.string.user_avatar),
                    title = user.userName,
                    width = 60.dp,
                    content = {
                        CardListButton(
                            text = stringResource(id = R.string.connect_to_user),
                            onClick = {
                                user.let { user ->
                                    connectionEvent(
                                        ContactCardConnectionEvent.InviteConnectEvent(
                                            user.userId,
                                            user.rowId
                                        )
                                    )
                                }
                            }
                        )
                    }
                )
            } else {
                ListSection(
                    image = R.drawable.ic_dashboard_black_24dp,
                    contentDescription = stringResource(id = R.string.user_avatar),
                    title = user.userName,
                    width = 60.dp,
                    content = {
                        TextRowAnimation(
                            visible = showInviteSentSuccess,
                            content = {
                                Text(
                                    text = stringResource(id = R.string.connect_invite_sent),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}

// TODO: remove just to test
fun testUser1(userId: String): User {
    return User.builder()
       // .userId(userId)
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
       // .userId(userId)
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
       // .userId(userId)
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
    val createdAt: String,
)

data class AlertDialogData(
    val icon: Int,
    val title: Int,
    val description: Int,
    val dialogIconDescription: Int,
    val dismiss: Int,
    val confirm: Int,
)
