package com.mccartycarclub

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mccartycarclub.ui.components.StartScreen
import com.mccartycarclub.ui.components.TopBar
import com.mccartycarclub.ui.viewmodels.MainViewModel
import com.mccartycarclub.utils.fetchUserId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StartScreen( // TODO: use a main compose screen change this
                acceptInvite = { // TODO: rename
                    mainViewModel.acceptContactInvite()
                },
                inviteContact = { userId ->
                    mainViewModel.inviteContact(
                        rowId = { rowId ->
                            // TODO: for testing
                            sendConnectInvite(
                                "Link Test, https://carclub.app",
                                "+15551234567",
                                rowId,
                            )
                        },
                        userId = userId,
                    )
                }
            )
            checkPermissions()
        }
        handleIncomingIntentS(intent)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadUserData()
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
                loadUserData()
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
    }

    private fun loadUserData() = mainViewModel.getDeviceContacts()

    private fun sendConnectInvite(message: String, phoneNumber: String, rowId: String) {
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
    }

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

    companion object {
        const val CAR_CLUB_URL = "https://carclub.app"
        const val MAIN_SCREEN = "main_screen"
        const val CONTACTS_SCREEN = "contacts_screen"
        const val GROUPS_SCREEN = "groups_screen"
        const val SEARCH_SCREEN = "search_screen"
    }
}