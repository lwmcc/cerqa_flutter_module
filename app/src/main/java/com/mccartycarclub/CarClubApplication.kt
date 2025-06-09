package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.mccartycarclub.R
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

            val amplifyOutputs = (AmplifyOutputs(R.raw.amplify_outputs)
                    as com.amplifyframework.core.configuration.AmplifyOutputs)
            Amplify.configure(amplifyOutputs, applicationContext)
        } catch (error: AmplifyException) {
            // TODO: log
        }
    }
}
