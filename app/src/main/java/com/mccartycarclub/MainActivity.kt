package com.mccartycarclub

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.ui.Authenticator
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Authenticator { state ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Hello ${state.user.username}!",
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Amplify.Auth.signOut { }
                        },
                    ) {
                        Text(text = "Sign Out")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            Amplify.Auth.fetchUserAttributes({ attributes ->
                                val userId = attributes.firstOrNull { it.key.keyString == "sub" }?.value

                                if (userId.isNullOrEmpty()) {
                                    Log.e("AmplifyUser", "User ID is null or empty!")
                                    return@fetchUserAttributes
                                }

                                val user = User.builder()
                                    .userId(userId)   // Required
                                    .firstName("Larry")  // Required
                                    .lastName("McCarty")  // Required
                                    .name("Larry M")  // Optional
                                    .phone("555-111-4545")  // Optional
                                    .userName("Larry Mc") // Optional
                                    .email("lwmccarty@gmail.com") // Optional
                                    .avatarUri("https://example.com/avatar.png") // Optional
                                    .id(userId)
                                    .build()
                                Amplify.API.mutate(
                                    ModelMutation.create(user),
                                    { response ->
                                        Log.i("MainActivity", "User created: ${response.data}")
                                    },
                                    { error ->
                                        Log.e("MainActivity", "User creation failed", error)
                                    }
                                )
                            }, { error ->
                                Log.e(
                                    "MainActivity *****", "Failed to fetch user attributes", error
                                )
                            })
                        }) {
                        Text(text = "Create User")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {

                        }) {
                        Text(text = "Add Contact")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            mainViewModel.inviteContact(state.user.userId, rowId = { rowId ->
                                sendConnectInvite(
                                    "Link Test, https://carclub.app",
                                    "555-521-5554",
                                    rowId,
                                )
                            })
                        }) {
                        Text(text = "Send Invite")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            mainViewModel.acceptContactInvite()
                        }) {
                        Text(text = "Accept Invite")
                    }

                }
                checkPermissions()
            }
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
    }

    fun testUser1(userId: String) = run {
        User.builder()
            .userId(userId)
            .firstName("Larry")
            .lastName("McCarty")
            .name("LM")
            .email("lwmccarty@gmail.com")
            .avatarUri("https://www.google.com")
            .phone("480-555-1212")
            .id(userId)
            .build()
    }

    fun testUser2(userId: String) = run {
        User.builder()
            .userId("fake-user-id")
            .firstName("Lebron")
            .lastName("James")
            .name("King James")
            .avatarUri("https://www.google.com")
            .phone("480-111-1212")
            .id(userId)
            .build()
    }
}