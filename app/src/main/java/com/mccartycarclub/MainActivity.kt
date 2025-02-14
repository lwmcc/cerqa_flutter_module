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
import com.amplifyframework.datastore.generated.model.Todo
import com.amplifyframework.datastore.generated.model.User

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
                        Amplify.Auth.signOut {  }
                    }) {
                        Text(text = "Sign Out")
                    }

                   // val query = ModelQuery.list(Todo::class.java)

                  // var todoList by remember { mutableStateOf(emptyList<Todo>()) }

                    Button(onClick = {

                        println("my click")
/*                        val user = User.builder()
                            .firstName("larry")
                            .lastName("mccarty")
                            .name("fake name")
                            .id("fake-id")
                            .email("lwmccarty@gmail.com")
                            .phone("480-392-6853")
                            .avatarUri("https://fake-uri.com")
                            .build()

                        Amplify.API.mutate(
                            ModelMutation.create(user),
                            { Log.i("MainActivity *****", "Added User with id: ${it}")},
                            { Log.e("MainActivity *****", "Create failed", it)},
                        )*/

                        val todo = Todo.builder()
                            .content("Another reset new user")
                            .build()

                       Amplify.API.mutate(
                            ModelMutation.create(todo),
                            { Log.i("MainActivity *****", "Added Todo with id:  ${it.data.id}")},
                            { Log.e("MainActivity *****", "Create failed", it)},
                        )
                    }) {
                        Text(text = "Create Todo")
                    }
                }
            }
        }
    }
}