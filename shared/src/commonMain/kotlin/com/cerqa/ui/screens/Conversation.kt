package com.cerqa.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cerqa.data.Preferences
import com.cerqa.viewmodels.ConversationViewModel
import com.cerqa.viewmodels.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun Conversation(
    receiverId: String,
    conversationViewModel: ConversationViewModel = koinInject(),
    preferences: Preferences = koinInject()
) {
    val senderId = preferences.getUserData()?.userId ?: ""
    val channelId = conversationViewModel.createConversationChannelName(receiverId, senderId)

    Chat("") { chatMessage ->
        conversationViewModel.sendChatMessage(
            channelId = channelId,
            senderUserId = senderId,
            receiverId = receiverId,
            message = chatMessage
        )
    }
}

@Composable
fun Chat(
    name: String,
    onSendChatMessage: (String) -> Unit,
) {
    var textState by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn {
                // Messages will go here
            }
        }

        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .windowInsetsPadding(WindowInsets.ime)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val messageToSend = textState.trim()
                        onSendChatMessage(messageToSend)
                        textState = ""
                    },
                    enabled = textState.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textState.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray
                    )
                }
            }
        }
    }
}
