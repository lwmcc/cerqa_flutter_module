package com.mccartycarclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.ui.Authenticator

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
           // Authenticator { state ->
                Column {
                    Text (
                        text = "my ttest"
                    )
/*                    Text(
                        text = "Hello ${state.user.username}!",
                    )
                    Button(onClick = {
                        Amplify.Auth.signOut {  }
                    }) {
                        Text(text = "Sign Out")
                    }*/
               // }
            }
        }
    }
}