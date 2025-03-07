package com.mccartycarclub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.ui.Authenticator
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserGroup
import com.mccartycarclub.domain.Member
import com.mccartycarclub.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import com.mccartycarclub.domain.Group  as GroupM
import com.mccartycarclub.domain.Contact as ContactM

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: to init viewmodel
        mainViewModel

        setContent {
            Authenticator { state ->
                Column {
                    Text(
                        text = "Hello ${state.user.username}!",
                    )

                    Button(onClick = {
                        Amplify.Auth.signOut { }
                    }) {
                        Text(text = "Sign Out")
                    }

                    val userId = UUID.randomUUID().toString()

                    Button(onClick = {

                        Amplify.Auth.fetchUserAttributes(
                            { attributes ->
                                val userId =
                                    attributes.firstOrNull { it.key.keyString == "sub" }?.value
                                Log.d("MainActivity *****", "User ID: $userId")

                                //  12121212-1031-7026-1ea5-9e5c424b27de
/*                                val user = User.builder()
                                    .userId(userId)
                                    .firstName("Larry")
                                    .lastName("McCarty")
                                    .name("LM")
                                    .email("lmccarty@outlook.com")
                                    .avatarUri("https://fake-uri.com")
                                    .phone("480-434-1135")
                                    .id(userId)
                                    .userName("LM")
                                    .build()

                                Amplify.API.mutate(
                                    ModelMutation.create(user),
                                    { Log.i("MainActivity *****", "Added User with id: ${it}") },
                                    { Log.e("MainActivity *****", "Create failed", it) },
                                )*/

                            },
                            { error ->
                                Log.e(
                                    "MainActivity *****",
                                    "Failed to fetch user attributes",
                                    error
                                )
                            }
                        )

                    }) {
                        Text(text = "Create Todo")
                    }
                }
            }
        }
    }
}