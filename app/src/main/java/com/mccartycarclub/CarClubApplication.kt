package com.mccartycarclub

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.pigeon.PigeonFlutterApi
import dagger.hilt.android.HiltAndroidApp
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel


@HiltAndroidApp
class CarClubApplication : Application() {

    lateinit var chatEngine: FlutterEngine
    lateinit var inboxEngine: FlutterEngine

    lateinit var pigeonFlutterApi: PigeonFlutterApi

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

        // Chat Engine
        chatEngine = FlutterEngine(this)
        chatEngine.navigationChannel.setInitialRoute(CHAT_INITIAL_ROUTE);
        chatEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put(CHAT_ENGINE_ID, chatEngine)

        /*
         * Initialize the Pigeon Flutter API in order to
         * send data from Android to Flutter
         */
        //pigeonFlutterApi = PigeonFlutterApi(chatEngine.dartExecutor.binaryMessenger)

        // Register the Android implementation of CerqaHostApi to handle calls
        // coming from Flutter and the binary messenger of the Flutter engine.
        // Letting Flutter invoke Android methods through Pigeon.
        // CerqaHostApi.setUp(chatEngine.dartExecutor.binaryMessenger, ChatHostApi())

        // Inbox Engine
        inboxEngine = FlutterEngine(this)
        inboxEngine.navigationChannel.setInitialRoute(INBOX_INITIAL_ROUTE);
        inboxEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put(INBOX_ENGINE_ID, inboxEngine)

        //MethodChannel(
        //    chatEngine.dartExecutor.binaryMessenger,
        //    "chat_method_channel"
        //).invokeMethod("amplifyToken", "message-flutter")
    }

    companion object {
        const val CHAT_ENGINE_ID = "chat_engine_id"
        const val INBOX_ENGINE_ID = "inbox_engine_id"
        const val CHAT_INITIAL_ROUTE = "/"
        const val CHAT_ROUTE = "chat"
        const val CONVERSATION_ROUTE = "conversation"
        const val GROUP_CHAT_ROUTE = "group_chat"
        const val GROUP_CONVERSATIONS_ROUTE = "group_conversation"
        const val INBOX_ROUTE = "inbox"
        const val INBOX_INITIAL_ROUTE = "/inbox"
    }
}
