package com.mccartycarclub.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.mccartycarclub.R

@Composable
fun ListSection(
    @DrawableRes image: Int,
    contentDescription: String,
    title: String,
    width: Dp,
    content: @Composable () -> Unit,
) {
    Row {
        AsyncImage(
            model = image,// "https://example.com/image.jpg",
            // TODO: add an image user.avatarUri
            contentDescription = contentDescription,
            modifier = Modifier
                .width(width)
                .padding(
                    dimensionResource(id = R.dimen.card_padding_start),
                    dimensionResource(id = R.dimen.card_padding_top),
                )
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            content()
        }
    }
}