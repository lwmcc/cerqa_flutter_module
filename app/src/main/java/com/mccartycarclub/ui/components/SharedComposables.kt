package com.mccartycarclub.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.SignedInState
import com.mccartycarclub.R
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.ui.viewmodels.MainViewModel

@Suppress("LongMethod")
@Composable
fun AppAuthenticator(
    state: SignedInState,
    topBarClick: (ClickNavigation) -> Unit,
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopBar(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            // TODO: to move somewhere this is for testing only
            //mainViewModel.setLoggedInUserId(state.user.username)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Hello ${state.user.username}!",
                )

                // All of the button below are just for testing
                // the backed, and creating and testing users. This will
                // be moved to an area for dev only
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        Amplify.Auth.signOut { }
                    },
                ) {
                    Text(text = "Sign Out")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // TODO: move, just for testing
                        Amplify.Auth.fetchUserAttributes({ attributes ->
                            val userId =
                                attributes.firstOrNull { it.key.keyString == "sub" }?.value

                            // TODO: move just for testing
                            /*                            Amplify.API.mutate(
                                                            ModelMutation.create(testUser3(userId!!)),
                                                            { response -> // TODO: response?
                                                                // This is were userId is added to prefs
                                                                mainViewModel.setLoggedInUserId(userId)
                                                            },
                                                            { error ->
                                                                Log.e("MainActivity *****", "User creation failed", error)
                                                            }
                                                        )*/
                        }, { error ->
                            Log.e(
                                "MainActivity *****", "Failed to fetch user attributes", error
                            )
                        })
                    }) {
                    Text(text = "Create User")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    }) {
                    Text(text = "Add Contact")
                }
            }
        }
    }
}

@Composable
fun Groups(topBarClick: (ClickNavigation) -> Unit) {
    Scaffold(
        topBar = {
            TopBarGroups(
                stringResource(id = R.string.app_name),
                topBarClick = {
                    topBarClick(it)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Text(text = "Groups")
        }
    }
}

@Composable
fun Notifications(topBarClick: (ClickNavigation) -> Unit) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Text(text = "Notifications")
        }
    }
}

@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional for debugging
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CHAT",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun NotificationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional for debugging
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NOTIFICATIONS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun GroupsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional for debugging
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GROUPS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun GroupsAddScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Optional for debugging
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GROUPS ADD",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}