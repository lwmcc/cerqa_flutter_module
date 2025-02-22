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
import com.amplifyframework.datastore.generated.model.AppData
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.Group
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserGroup
import com.mccartycarclub.domain.Member
import java.util.UUID
import com.mccartycarclub.domain.Group  as GroupM
import com.mccartycarclub.domain.Contact as ContactM

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

                    // Mock datta
                    val groups = mutableListOf<GroupM>()
                    val members = mutableListOf<Member>()
                    val contacts = mutableListOf<ContactM>()

                    members.add(
                        Member("", "")
                    )

                    members.add(
                        Member("", "")
                    )

                    contacts.add(
                        ContactM("")
                    )

                    contacts.add(
                        ContactM("")
                    )

                    groups.add(
                        GroupM(true, members)
                    )

                    Button(onClick = {

                        Amplify.Auth.fetchUserAttributes(
                            { attributes ->
                                val userId =
                                    attributes.firstOrNull { it.key.keyString == "sub" }?.value
                                Log.d("MainActivity *****", "User ID: $userId")

                                val appData = AppData.builder()
                                    .firstName("Larry")
                                    .lastName("McCarty")
                                    .name("Larry M")
                                    .email("lwmccarty@gmail.com")
                                    .avatarUri("https://fake-uri.com")
                                    .phone("480-392-6853")
                                    .userName("fake user name")
                                    .type("User")
                                    .userId(userId)
                                    .contacts("")
                                    .groups("")
                                    .vehicles("")
                                    .build()

                                /*
                                val appData = App.builder()
                                    .firstName("Larry")
                                    .lastName("McCarty")
                                    .name("Larry M")
                                    .id("id-test")
                                    .email("lwmccarty@gmail.com")
                                    .avatarUri("https://fake-uri.com")
                                    .phone("480-392-6853")
                                    .build()*/


                                val user = User.builder()
                                    .firstName("Larry")
                                    .lastName("McCarty")
                                    .name("Larry M")
                                    .id("id-test")
                                    .email("lwmccarty@gmail.com")
                                    .avatarUri("https://fake-uri.com")
                                    .phone("480-392-6853")
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

}