package com.mccartycarclub

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amplifyframework.ui.authenticator.ui.Authenticator as Authenticator
import com.mccartycarclub.data.websocket.AblyPushMessagingService
import com.mccartycarclub.ui.components.auth.AuthenticatorStateProvider
import com.mccartycarclub.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.net.toUri
import com.mccartycarclub.ui.start.StartScreen
import com.mccartycarclub.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var pushReceiver: BroadcastReceiver

    @Inject
    lateinit var stateProvider: AuthenticatorStateProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(tonalElevation = 5.dp) {
                    Authenticator(
                        state = stateProvider.provide(),
                        modifier = Modifier.fillMaxHeight(),
                    ) { state ->
                        mainViewModel.setLoggedInUserId(
                            userId = state.user.userId,
                            userName = state.user.username,
                        )
                        StartScreen(
                            mainViewModel,
                            state,
                            topBarClick = {

                            },
                            sendSms = { message ->
                                sendSms(
                                    context = this@MainActivity,
                                    message = message.message,
                                    title = message.title,
                                    phoneNumber = message.phoneNumber,
                                )
                            },
                        )
                        checkPermissions()
                    }
                }
            }
        }
        registerReceiver()
        handleIncomingIntentS(intent)
        //mainViewModel.initAbly()
    }

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(pushReceiver, IntentFilter("io.ably.broadcast.PUSH_ACTIVATE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            pushReceiver, IntentFilter(
                AblyPushMessagingService.PUSH_NOTIFICATION_ACTION
            )
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
          //  loadUserData()
        } else {
            // TODO: show message stating my permission is needed
            println("MainActivity ***** ACCESS DENIED")
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this@MainActivity, android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
             //   loadUserData()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, android.Manifest.permission.READ_CONTACTS
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                //showInContextUI(...)
                println("MainActivity ***** SHOW WHY IT IS NEEDED")
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_CONTACTS
                )
                println("MainActivity ***** ASK FOR PERMISSION")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                println("MainActivity *** CAN POST NOTIFICATIONS")
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                println("MainActivity *** SHOW REASONS FOR REQUEST")

                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                println("MainActivity *** REQUST PERMISSION LAUNCHER")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun sendSms(context: Context, message: String, title: String, phoneNumber: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
            putExtra("sms_body", message)
        }
        val chooser: Intent = Intent.createChooser(smsIntent, title)
        try {
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            // TODO: show banner
        }
    }
   // private fun loadUserData() = mainViewModel.getDeviceContacts()

    /*    private fun sendConnectInvite(message: String, phoneNumber: String, rowId: String) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mmsto:$phoneNumber")
                putExtra("sms_body", message)
                putExtra("row_id", rowId)
                // putExtra(Intent.EXTRA_STREAM, attachment)
            }
            // TODO: does not get passed if
            //if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            //}
        }*/

    private fun handleIncomingIntentS(intent: Intent) {
        if (intent.action.equals(LinkActions.ACTION_VIEW.action)) {
            when (intent.data.toString()) {
                CAR_CLUB_URL -> {
                    // TODO: implement dialog
                    // user is inviting a contact to connnect in the app
                    // user 2 can accept or deny
                    // if accepted get  user 2 user id, get user 1 user id
                    // add user 2 to user 1 contacts
                    // add user 1 to user 2 contacts
                    // send message to user 1

                    val rosId = intent.extras?.getString("row_id")
                    println("MainActivity ***** SHOW CONTACTS DIALOG ID $rosId")
                }
            }
        }
    }

    enum class LinkActions(val action: String) {
        ACTION_VIEW("android.intent.action.VIEW")
    }

    // TODO: move to enum
    companion object {
        const val CAR_CLUB_URL = "https://carclub.app"
        const val MAIN_SCREEN = "main_screen"
        const val CONTACTS_SCREEN = "contacts_screen"
        const val GROUPS_SCREEN = "groups_screen"
        const val SEARCH_SCREEN = "search_screen"
        const val NOTIFICATIONS_SCREEN = "notifications_screen"
    }
}
