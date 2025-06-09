package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.mccartycarclub.R.raw as Raw
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CarClubApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(AmplifyOutputs(Raw.amplify_outputs), applicationContext)
        } catch (error: AmplifyException) {
            // TODO: log
        }
    }
}
