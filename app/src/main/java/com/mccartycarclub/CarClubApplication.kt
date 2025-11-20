package com.mccartycarclub

import android.app.Application
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.configuration.AmplifyOutputs
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.pigeon.CerqaHostApi
import com.mccartycarclub.pigeon.ChatHostApi
import com.mccartycarclub.pigeon.PigeonFlutterApi
import com.mccartycarclub.repository.ChatRepository
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.LocalRepository
import dagger.hilt.android.HiltAndroidApp
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class CarClubApplication : Application() {

    private var chatEngine: FlutterEngine? = null
    private var inboxEngine: FlutterEngine? = null

    lateinit var pigeonFlutterApi: PigeonFlutterApi

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    @Named("IoDispatcher")
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var contactsRepository: ContactsRepository

    @Inject
    lateinit var localRepository: LocalRepository

    override fun onCreate() {
        super.onCreate()

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

        // Flutter engines will be initialized lazily when needed to reduce memory usage
        // See getChatEngine() and getInboxEngine() methods

        //MethodChannel(
        //    chatEngine.dartExecutor.binaryMessenger,
        //    "chat_method_channel"
        //).invokeMethod("amplifyToken", "message-flutter")
    }

    fun getChatEngine(): FlutterEngine {
        if (chatEngine == null) {
            chatEngine = FlutterEngine(this).apply {
                navigationChannel.setInitialRoute(CHAT_INITIAL_ROUTE)

                // Register Android to handle message through binaryMessenger BEFORE executing Dart
                CerqaHostApi.setUp(
                    dartExecutor.binaryMessenger,
                    ChatHostApi(
                        chatRepository,
                        contactsRepository,
                        localRepository,
                        ioDispatcher,
                    ),
                )

                // Now execute Dart entrypoint after message handlers are registered
                dartExecutor.executeDartEntrypoint(
                    DartExecutor.DartEntrypoint.createDefault()
                )
            }
            FlutterEngineCache.getInstance().put(CHAT_ENGINE_ID, chatEngine!!)
        }
        return chatEngine!!
    }

    fun getInboxEngine(): FlutterEngine {
        if (inboxEngine == null) {
            inboxEngine = FlutterEngine(this).apply {
                navigationChannel.setInitialRoute(INBOX_INITIAL_ROUTE)
                dartExecutor.executeDartEntrypoint(
                    DartExecutor.DartEntrypoint.createDefault()
                )
            }
            FlutterEngineCache.getInstance().put(INBOX_ENGINE_ID, inboxEngine!!)
        }
        return inboxEngine!!
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
