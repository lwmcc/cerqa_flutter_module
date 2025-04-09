package com.mccartycarclub.ui.components

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.MainActivity.Companion.CONTACTS_SCREEN
import com.mccartycarclub.MainActivity.Companion.GROUPS_SCREEN
import com.mccartycarclub.MainActivity.Companion.MAIN_SCREEN
import com.mccartycarclub.MainActivity.Companion.SEARCH_SCREEN
import com.mccartycarclub.R
import com.mccartycarclub.navigation.AppNavigationActions
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.NetResult
import com.mccartycarclub.ui.viewmodels.MainViewModel
import com.mccartycarclub.utils.fetchUserId
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun StartScreen(
    acceptInvite: () -> Unit,
    inviteContact: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
    navActions: AppNavigationActions = remember(navController) {
        AppNavigationActions(navController)
    }
) {
    NavHost(navController = navController, startDestination = MAIN_SCREEN) {
        composable(MAIN_SCREEN) {
            AppAuthenticator(
                acceptInvite,
                inviteContact,
                topBarClick = {
                    navToScreen(it, navActions)
                }
            )
        }

        composable(CONTACTS_SCREEN) { backStackEntry ->
            Contacts(topBarClick = {
                navToScreen(it, navActions)
            })
        }

        composable(GROUPS_SCREEN) { backStackEntry ->
            Groups(topBarClick = {
                navToScreen(it, navActions)
            })
        }

        composable(SEARCH_SCREEN) { backStackEntry ->
            Search(topBarClick = {
                navToScreen(it, navActions)
            })
        }
    }
}

private fun navToScreen(
    clickNavigation: ClickNavigation,
    navActions: AppNavigationActions,
) {
    when (clickNavigation) {
        ClickNavigation.NavToContacts -> {
            navActions.navigateToContacts()
        }

        ClickNavigation.NavToGroups -> {
            navActions.navigateToGroups()
        }

        ClickNavigation.NavToSearch -> {
            navActions.navigateToSearch()
        }

        ClickNavigation.PopBackstack -> {
            navActions.popBackStack()
        }
    }
}

@Composable
fun AppAuthenticator(
    acceptInvite: () -> Unit,
    inviteContact: (String) -> Unit,
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
            com.amplifyframework.ui.authenticator.ui.Authenticator { state ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Hello ${state.user.username}!",
                    )

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

                            Amplify.Auth.fetchUserAttributes({ attributes ->
                                val userId =
                                    attributes.firstOrNull { it.key.keyString == "sub" }?.value

                                println("MainActivity ***** USER ID $userId")

                                Amplify.API.mutate(
                                    ModelMutation.create(testUser2(userId!!)),
                                    { response ->
                                        Log.i("MainActivity", "User created: ${response.data}")
                                        println("MainActivity ***** ERROR ${response.hasErrors()}")
                                        println("MainActivity ***** ERROR ${response.errors}")
                                    },
                                    { error ->
                                        Log.e("MainActivity", "User creation failed", error)
                                    }
                                )
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

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            inviteContact(state.user.userId)
                        }) {
                        Text(text = "Send Invite")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            acceptInvite
                        }) {
                        Text(text = "Accept Invite")
                    }

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    appBarTitle: String,
    topBarClick: (ClickNavigation) -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = appBarTitle)
        },
        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToGroups)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_groups), // Use your drawable resource
                    contentDescription = "Localized description"
                )
            }
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToContacts)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_contacts), // Use your drawable resource
                    contentDescription = "Localized description"
                )
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
            Text(text = appBarTitle)
        },
        actions = {
            IconButton(
                onClick = {
                    topBarClick(ClickNavigation.NavToSearch)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_action_search),
                    contentDescription = "Localized description"
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
            Text(text = appBarTitle)
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
            Text(text = appBarTitle)
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

fun testUser1(userId: String) = run {
    User.builder()

        .firstName("Larry")
        .lastName("McCarty")
        .name("LM")
        .email("lwmccarty@gmail.com")
        .avatarUri("https://www.google.com")
        .phone("+15551114545")
        .userId(userId)
        //.id(userId)
        .build()
}

@Composable
fun Contacts(mainViewModel: MainViewModel = hiltViewModel(), topBarClick: (ClickNavigation) -> Unit) {
    Scaffold(
        topBar = {
            TopBarContacts(
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
            Text(text = "Contacts")
            fetchUserId {
                if (it.loggedIn) {
                    it.userId?.let { userId ->
                        mainViewModel.fetchUserContacts(userId)
                    }
                } else {
                    // TODO: log message
                }
            }
        }
    }
}

@Composable
fun Groups(mainViewModel: MainViewModel = hiltViewModel(), topBarClick: (ClickNavigation) -> Unit) {
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
fun Search(
    mainViewModel: MainViewModel = hiltViewModel(),
    topBarClick: (ClickNavigation) -> Unit,
) {
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

        val searchQuery = mainViewModel.searchResults.collectAsStateWithLifecycle().value
        var input by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            TextField(
                value = input,
                maxLines = 2,
                onValueChange = {
                    input = it
                    mainViewModel.onQueryChange(it)
                },
                label = { Text(stringResource(id = R.string.user_search)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
            )

            when (searchQuery) {
                NetResult.Pending -> {
                    Pending()
                }

                is NetResult.Success -> {
                    //val user = (searchQuery).data as User
                    val user = (searchQuery as? NetResult.Success)?.data
                    UserCard(user, onCardClick = {
                        mainViewModel.createConnectInvite(it)
                        println("StartScreen ***** USER ID $it")
                    })
                }

                is NetResult.Error -> {

                }
            }

        }
    }
}

@Composable
fun Pending() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun Error() {
    Column {

    }
}

@Composable
fun UserCard(user: User?, onCardClick: (String?) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        onClick = {
            onCardClick(user?.userId)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.card_padding)),
    ) {
        Row {
            AsyncImage(
                model = "https://example.com/image.jpg", // TODO: add an image user.avatarUri
                contentDescription = "Translated description of what the image contains"
            )
            Column(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.card_padding_start),
                    dimensionResource(id = R.dimen.card_padding_top),
                )
            ) {
                user?.userName?.let { Text(it) }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_height)))
                user?.name?.let { Text(it) }
            }
        }
    }
}

fun testUser2(userId: String): User {
    return User.builder()
        .firstName("Lebron")
        .lastName("James")
        .name("Lebron J")
        .phone("+15555554545")
        .userName("KingJames")
        .email("lmccarty@outlook.com")
        .avatarUri("https://example.com/avatar.png")
        .userId(userId)
        .id(userId)
        .build()
}

data class Ids(
    val rowId: String,
    val userId: String,
)