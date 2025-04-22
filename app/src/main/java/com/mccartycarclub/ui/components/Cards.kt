package com.mccartycarclub.ui.components

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mccartycarclub.R


@Composable
fun ContactCard(
    // TODO: reduce number of params with data class
    firstLine: String,
    secondLine: String,
    thirdLine: String,
    buttonPair: Boolean,
    primaryButtonText: String,
    secondaryButtonText: String,
    @DrawableRes avatar: Int,
    onClick: (ContactCardActions) -> Unit,
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
                Text(text = firstLine)
                Text(text = secondLine)
                Text(text = thirdLine)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
        ) {

            if (buttonPair) {
                ContactCardButton(primaryButtonText, onClick = {
                    onClick(ContactCardActions.DeleteReceivedInvite(""))
                })

                ContactCardButton(secondaryButtonText, onClick = {
                    onClick(ContactCardActions.Connect(""))
                })
            } else {
                ContactCardButton(secondaryButtonText, onClick = {
                    onClick(ContactCardActions.Connect(""))
                })
            }
        }
    }
}

@Preview
@Composable
fun ContactCardPreview() {
    ContactCard(
        firstLine = "LM",
        secondLine = "Larry",
        thirdLine = "4-21-2025",
        buttonPair = true,
        primaryButtonText = "Cancel",
        secondaryButtonText = "Connect",
        avatar = R.drawable.ic_dashboard_black_24dp,
        onClick = { data ->

        }
    )
}

@Composable
fun ContactCardButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(text)
    }
}