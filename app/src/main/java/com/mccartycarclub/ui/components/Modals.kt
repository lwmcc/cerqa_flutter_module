package com.mccartycarclub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetList(
    phoneNumbers: List<String>,
    onDismissRequest: () -> Unit,
    onClick: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn {
            items(phoneNumbers) { item ->
                Column(
                    modifier = Modifier.clickable(
                        onClick = {
                            onClick(item)
                        }
                    )) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
