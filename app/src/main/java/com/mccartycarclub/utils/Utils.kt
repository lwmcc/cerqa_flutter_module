package com.mccartycarclub.utils

import com.amplifyframework.api.ApiException
import com.amplifyframework.core.Amplify
import com.amplifyframework.kotlin.core.Amplify as CoreAmplify
import java.util.UUID


fun getUuid() = UUID.randomUUID().toString()

fun fetchUserId(loggedIn: (LoggedIn) -> Unit) {
    println("Shared ***** attributes CALL")
    // TODO: inject Amplify
    Amplify.Auth.fetchUserAttributes({ attributes ->
        println("Shared ***** attributes ${attributes}")
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

suspend fun fetchUserIdv2() {
    try {
        CoreAmplify.Auth.fetchUserAttributes()
            .firstOrNull { it.key.keyString == "sub" }?.value
    } catch (e: Exception) {
        println("Utils ***** ${e.message} CAUSE ${e.cause?.message}")

        /*
                 aws.smithy.kotlin.runtime.http.HttpException:
                 java.net.UnknownHostException: Unable to resolve host "cognito-idp.us-east-1.amazonaws.com": No address associated with hostname CAUSE java.net.UnknownHostException: Unable to resolve host "cognito-idp.us-east-1.amazonaws.com": No address associated with hostname

         */
    }
}



data class LoggedIn(
    val loggedIn: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null,
)