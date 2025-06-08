package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.core.configuration.AmplifyOutputs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarClubApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(
                AmplifyOutputs(R.raw.amplify_outputs), applicationContext
            )
            // TODO: log
        } catch (error: AmplifyException) {
            // TODO: log
        }
    }
}
