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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    userImageUrl: String? = null,
    onDismiss: () -> Unit = {},
    viewModel: ProfileViewModel = koinInject()
) {

    val isProfileComplete by viewModel.isProfileComplete.collectAsState()
    val userData by viewModel.userData.collectAsState()

    var showCreateProfileForm by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newFirstName by remember { mutableStateOf("") }
    var newLastName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.checkProfileComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shadowElevation = 8.dp
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Profile") },
                            actions = {
                                if (isProfileComplete == false) {
                                    IconButton(onClick = {
                                        // Reset fields on delete
                                        newUserName = ""
                                        newFirstName = ""
                                        newLastName = ""
                                        newPhone = ""
                                        newEmail = ""
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.createUser(
                                            userName = newUserName,
                                            firstName = newFirstName,
                                            lastName = newLastName,
                                            phone = newPhone,
                                            email = newEmail
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = "Save"
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
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

                        Spacer(modifier = Modifier.height(16.dp))

                        if (userData != null) {
                            // Show saved user data from preferences
                            val currentUserData = userData
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentUserData?.userName ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = currentUserData?.userEmail ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = currentUserData?.createdAt ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                if (isProfileComplete == false) {
                                    Text(
                                        text = "Profile incomplete",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else if (isProfileComplete == false) {
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

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newLastName,
                                onValueChange = { newLastName = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newPhone,
                                onValueChange = { newPhone = it },
                                label = { Text("Phone") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(
                            onClick = {
                                viewModel.logout()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}