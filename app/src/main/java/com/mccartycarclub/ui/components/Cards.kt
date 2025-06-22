package com.mccartycarclub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mccartycarclub.R
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.ReceivedContactInvite

@Composable
fun CurrentContactCard(
    contact: Contact,
    @DrawableRes avatar: Int,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = avatar,// "https://example.com/image.jpg",
                // TODO: add an image user.avatarUri
                contentDescription = stringResource(id = R.string.user_avatar),
                modifier = Modifier
                    .width(60.dp)
                    .padding(
                        dimensionResource(id = R.dimen.card_padding_start),
                        dimensionResource(id = R.dimen.card_padding_top),
                    ),
                /*colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onError)*/
            )

            Column {
                Text(
                    text = contact.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun SentContactCard(
    contact: Contact,
    primaryButtonText: String,
    @DrawableRes avatar: Int,
    onClick: (ContactCardEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = avatar,// "https://example.com/image.jpg",
                // TODO: add an image user.avatarUri
                contentDescription = stringResource(id = R.string.user_avatar),
                modifier = Modifier
                    .width(60.dp)
                    .padding(
                        dimensionResource(id = R.dimen.card_padding_start),
                        dimensionResource(id = R.dimen.card_padding_top),
                    )
            )

            Column {
                Text(
                    text = contact.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${stringResource(R.string.connect_invite_sent)}${contact.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.card_secondary_text_corner_radius)))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(dimensionResource(id = R.dimen.card_secondary_text_padding)),
                )

                CardListButton(primaryButtonText, onClick = {
                    onClick(ContactCardEvent.CancelSentInvite(contact.userId))
                })
            }
        }
    }
}

@Composable
fun ReceivedInviteContactCard(
    contact: ReceivedContactInvite,
    primaryButtonText: String,
    secondaryButtonText: String,
    @DrawableRes avatar: Int,
    onDismissClick: (ContactCardEvent) -> Unit,
    onConfirmClick: (ContactCardEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("contactItem")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = avatar,// "https://example.com/image.jpg",
                // TODO: add an image user.avatarUri
                contentDescription = stringResource(id = R.string.user_avatar),
                modifier = Modifier
                    .width(dimensionResource(id = R.dimen.card_profile_icon_width))
                    .padding(
                        dimensionResource(id = R.dimen.card_padding_start),
                        dimensionResource(id = R.dimen.card_padding_top),
                    )
            )

            Column {
                Text(
                    text = contact.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${stringResource(R.string.connect_invite_received)}${contact.createdAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.card_secondary_text_corner_radius)))
                        .background(MaterialTheme.colorScheme.outline)
                        .padding(dimensionResource(id = R.dimen.card_secondary_text_padding)),
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    CardListButton(primaryButtonText, onClick = {
                        onDismissClick(ContactCardEvent.DeleteReceivedInvite(contact.userId))
                    })

                    Spacer(modifier = Modifier.width(8.dp))

                    CardListButton(
                        secondaryButtonText,
                        onClick = {
                            onConfirmClick(
                                ContactCardEvent.AcceptConnection(
                                    ConnectionAccepted(
                                        name = contact.name,
                                        userName = contact.userName,
                                        senderUserId = contact.userId,
                                        avatarUri = contact.avatarUri,
                                        userId = contact.contactId,
                                        createdAt = contact.createdAt.toString(),
                                    )
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CardListButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
