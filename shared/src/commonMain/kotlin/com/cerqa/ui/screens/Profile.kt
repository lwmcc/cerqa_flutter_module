package com.cerqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cerqa.data.Preferences
import com.cerqa.models.UserData
import com.cerqa.viewmodels.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun Profile(
    userImageUrl: String? = null,
    onDismiss: () -> Unit = {},
    viewModel: ProfileViewModel = koinInject(),
    preferences: Preferences = koinInject()
) {

    val isProfileComplete by viewModel.isProfileComplete.collectAsState()
    var userData by remember { mutableStateOf<UserData?>(null) }

    var showCreateProfileForm by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newFirstName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.checkProfileComplete()
        userData = preferences.getUserData()
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                }
        )

        Surface(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!userImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = userImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                val currentUser = userData
                if (isProfileComplete == true && currentUser != null) {
                    Text(
                        text = "${currentUser.userName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else if (isProfileComplete == false) {
                    Text(
                        text = "You",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showCreateProfileForm) {
                        OutlinedTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newFirstName,
                            onValueChange = { newFirstName = it },
                            label = { Text("First Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    showCreateProfileForm = false
                                    // Reset fields on cancel if desired
                                    newUserName = ""
                                    newFirstName = ""
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    // TODO: Call ViewModel to save profile
                                    // viewModel.createProfile(newUserName, newFirstName)
                                    showCreateProfileForm = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    } else {
                        // --- BUT
                    }
                } else {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}