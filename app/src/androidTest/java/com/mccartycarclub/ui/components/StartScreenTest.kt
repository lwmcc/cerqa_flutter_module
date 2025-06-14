package com.mccartycarclub.ui.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.ui.authenticator.AuthenticatorState
import com.amplifyframework.ui.authenticator.SignedInState
import com.amplifyframework.ui.authenticator.enums.AuthenticatorStep.SignedIn
import com.mccartycarclub.ui.components.auth.AppAuthenticator
import com.mccartycarclub.fakes.TestActivity
import com.mccartycarclub.ui.theme.AppTheme
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class StartScreenTest {

    private val contactsDescription = "View your contacts"

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_CONTACTS,
    )

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @BindValue
    @JvmField
    val fakeAuthenticatorState: AuthenticatorState = mockk {
        every { stepState } returns mockk<SignedInState> {
            every { user } returns AuthUser("userName", "user")
            every { step } returns SignedIn
        }
        every { messages } returns emptyFlow()
    }

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            AppTheme {
                AppAuthenticator(
                    state = fakeAuthenticatorState
                ) { state ->
                    StartScreen(state)
                }
            }
        }
    }

    @Test
    fun verifyNavigationIconsExist() {
        composeTestRule.onNodeWithContentDescription("View your groups").assertExists()
        composeTestRule.onNodeWithContentDescription(contactsDescription).assertExists()
        composeTestRule.onNodeWithContentDescription("View your notifications").assertExists()
    }

    @Test
    fun verifyNavigationContactsClick() {
        composeTestRule.onNodeWithContentDescription(contactsDescription).performClick()
    }
}
