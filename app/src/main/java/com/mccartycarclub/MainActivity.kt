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
                                val user = User.builder()
                                    .firstName("Kobe")
                                    .lastName("Bryant")
                                    .name("Mamba")
                                    .email("kobe@gmail.com")
                                    .avatarUri("https://fake-uri.com")
                                    .phone("480-777-1155")
                                    .id("246655-1031-7026-1ea5-9e5c424b27de")
                                    //.userName("Larry M")
                                    //.userId(userId)
                                    //.contacts(jsonString)
                                    //.groups(jsonGroups)
                                    //.vehicles("")
                                    .build()
                                Amplify.API.mutate(
                                    ModelMutation.create(user),
                                    { Log.i("MainActivity *****", "Added User with id: ${it}") },
                                    { Log.e("MainActivity *****", "Create failed", it) },
                                )

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

    companion object {
        val jsonString = """
            {
              "Contact": {
                "type": "model",
                "properties": {
                  "id": { "type": "id" },
                  "name": { "type": "string" },
                  "phone": { "type": "phone" },
                  "email": { "type": "email" },
                  "user": { "type": "belongsTo", "target": "User", "targetKey": "id" }
                },
                "authorization": {
                  "allow": ["guest"]
                }
              },
              "contacts": [
                {
                  "id": "1",
                  "name": "Alice Johnson",
                  "phone": "+11234567890",
                  "email": "alice.johnson@example.com"
                },
                {
                  "id": "2",
                  "name": "Bob Lee",
                  "phone": "+19876543210",
                  "email": "bob.lee@example.com"
                }
              ]
            }
            """

        val jsonGroups = """
            {
              "name": "Sample Contact List",
              "users": [
                {
                  "id": "1",
                  "avatarUri": "https://example.com/avatars/alice.jpg",
                  "email": "alice.johnson@example.com",
                  "firstName": "Alice",
                  "lastName": "Johnson",
                  "name": "Alice Johnson",
                  "phone": "+11234567890"
                },
                {
                  "id": "2",
                  "avatarUri": "https://example.com/avatars/bob.jpg",
                  "email": "bob.lee@example.com",
                  "firstName": "Bob",
                  "lastName": "Lee",
                  "name": "Bob Lee",
                  "phone": "+19876543210"
                }
              ]
            }
            """

    }

}