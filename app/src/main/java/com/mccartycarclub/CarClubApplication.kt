package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import com.cerqa.di.initKoin
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.LocalRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidContext
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class CarClubApplication : Application() {

    @Inject
    @Named("IoDispatcher")
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var contactsRepository: ContactsRepository

    @Inject
    lateinit var localRepository: LocalRepository

    override fun onCreate() {
        super.onCreate()

        // Init Koin for shared KMP module
        initKoin {
            androidContext(this@CarClubApplication)
        }

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())

            val amplifyOutputs = AmplifyOutputs(resourceId = R.raw.amplify_outputs)
            Amplify.configure(amplifyOutputs, applicationContext)
            println("CarClubApplication ***** Amplify configured successfully")
        } catch (error: AmplifyException) {
            println("CarClubApplication ***** Error Starting Amplify: ${error.message}")
            error.printStackTrace()
        }
    }
}
