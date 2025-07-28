package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import dagger.hilt.android.HiltAndroidApp
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor


@HiltAndroidApp
class CarClubApplication : Application() {

    lateinit var flutterEngine: FlutterEngine

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())

            val amplifyOutputs = AmplifyOutputs(resourceId = R.raw.amplify_outputs)
            Amplify.configure(amplifyOutputs, applicationContext)
        } catch (error: AmplifyException) {
            // TODO: log
            println("CarClubApplication ***** Error Starting Amplify")
        }

        flutterEngine = FlutterEngine(this)
        //flutterEngine.navigationChannel.setInitialRoute(INITIAL_ROUTE);

        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        FlutterEngineCache.getInstance().put(CERQA_ENGINE_ID, flutterEngine)
    }

    companion object {
        const val CERQA_ENGINE_ID = "cerqa_engine_id"
        const val INITIAL_ROUTE = "/"
        const val CHAT_HOME_ROUTE = "/chat_home"
        const val CHAT_ROUTE = "chat"
        const val CONVERSATION_ROUTE = "conversation"
        const val GROUP_CHAT_ROUTE = "group_chat"
        const val GROUP_CONVERSATIONS_ROUTE = "group_conversation"
        const val INBOX_ROUTE = "inbox"
    }
}
