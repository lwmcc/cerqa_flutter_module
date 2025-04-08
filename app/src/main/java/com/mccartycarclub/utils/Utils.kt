package com.mccartycarclub.utils

import android.util.Log
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.mccartycarclub.ui.components.testUser2
import java.util.UUID


fun getUuid() = UUID.randomUUID().toString()

fun fetchUserId(loggedIn: (LoggedIn) -> Unit) {
    Amplify.Auth.fetchUserAttributes({ attributes ->
        loggedIn(
            LoggedIn(
                loggedIn = true,
                userId = attributes.firstOrNull { it.key.keyString == "sub" }?.value
            )
        )
    }, { error ->
        loggedIn(LoggedIn(loggedIn = false, errorMessage = error.message))
    })
}

data class LoggedIn(
    val loggedIn: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null,
)