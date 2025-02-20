package com.mccartycarclub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.ui.Authenticator
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.Group
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserGroup
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                    // val query = ModelQuery.list(Todo::class.java)

                    // var todoList by remember { mutableStateOf(emptyList<Todo>()) }

                    val userId = UUID.randomUUID().toString()

                    Button(onClick = {

                        println("my click")


                        val group = Group.builder()
                            .name("Test group")
                            .id("1")
                            .build()

                        val contact = Contact.builder()
                            .id("83d16d49-37ab-4708-bf55-6d2a6c88450e")
                            .name("Johnny")
                            .phone("480-234-1111")
                            .email("smith@gmail.com")

                        val user2 = User.builder()
                            .firstName("Larry")
                            .lastName("McCarty")
                            .name("LM")
                            .id(userId)
                            .email("lwmccarty@gmail.com")
                            .avatarUri("https://fake-uri.com")
                            .phone("480-392-1111")
                            .build()

                        val user = User.builder()
                            .firstName("John")
                            .lastName("Smith")
                            .name("Johnny")
                            .id(userId)
                            .email("smith@gmail.com")
                            .avatarUri("https://fake-uri.com")
                            .phone("480-234-1111")
                            .build()

                        val userGroup =  UserGroup.builder()
                            .id("")
                            .user(user2)
                            .group(group)

                        Amplify.Auth.fetchUserAttributes(
                            { attributes ->
                                val userId =
                                    attributes.firstOrNull { it.key.keyString == "sub" }?.value
                                Log.d("MainActivity *****", "User ID: $userId")
                            },
                            { error ->
                                Log.e(
                                    "MainActivity *****",
                                    "Failed to fetch user attributes",
                                    error
                                )
                            }
                        )

                        Amplify.API.mutate(
                            ModelMutation.create(user2),
                            { Log.i("MainActivity *****", "Added User with id: ${it}") },
                            { Log.e("MainActivity *****", "Create failed", it) },
                        )
                    }) {
                        Text(text = "Create Todo")
                    }
                }
            }
        }
    }
}