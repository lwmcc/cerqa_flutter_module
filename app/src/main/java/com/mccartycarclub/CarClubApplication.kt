package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import com.cerqa.di.appModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.core.context.GlobalContext.startKoin

@HiltAndroidApp
class CarClubApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())

            val amplifyOutputs = AmplifyOutputs(resourceId = R.raw.amplify_outputs)
            Amplify.configure(amplifyOutputs, applicationContext)
        } catch (error: AmplifyException) {
            // TODO: log
        }

        startKoin {
            modules(appModule)
        }
    }
}
