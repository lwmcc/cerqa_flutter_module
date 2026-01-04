package com.cerqa.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModalBottomSheet() {

    Column {
        Button(
            onClick = {

            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Take photo")
        }
    }

    Button(
        onClick = {

        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Select photo")
    }

    Spacer(
        modifier = Modifier
            .height(16.dp)
            .fillMaxWidth(),
    )

    Button(
        onClick = {

        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Delete photo")
    }
}