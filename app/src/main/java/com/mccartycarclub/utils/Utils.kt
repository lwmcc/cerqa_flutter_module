package com.mccartycarclub.utils

import com.amplifyframework.core.Amplify
import com.amplifyframework.kotlin.core.Amplify as CoreAmplify
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

suspend fun fetchUserId() = CoreAmplify.Auth.fetchUserAttributes()
    .firstOrNull { it.key.keyString == "sub" }?.value


data class LoggedIn(
    val loggedIn: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null,
)