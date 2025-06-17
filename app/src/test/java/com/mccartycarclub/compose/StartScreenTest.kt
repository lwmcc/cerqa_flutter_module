package com.mccartycarclub.compose

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.mccartycarclub.repository.AmplifyRepo.Companion.DUMMY
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.testdoubles.receivedInvites
import com.mccartycarclub.ui.components.Contacts
import com.mccartycarclub.ui.viewmodels.ContactsViewModel
import com.mccartycarclub.ui.viewmodels.UiState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [33])
class StartScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    val mockRepo = mockk<RemoteRepo>(relaxed = true)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `verify received user invite`() {
        val contactsViewModel = mockk<ContactsViewModel>(relaxed = true)

        every { mockRepo.fetchAllContacts(DUMMY) } returns flowOf(
            NetworkResponse.Success(
                receivedInvites
            )
        )
        every { contactsViewModel.userId } returns MutableStateFlow(DUMMY)
        every { contactsViewModel.uiState } returns UiState(
            pending = false,
            contacts = receivedInvites
        )

        composeTestRule.setContent {
            Contacts(
                contactsViewModel,
                topBarClick = {}
            )
        }

        composeTestRule.onNodeWithText("LarryM").assertExists()
        composeTestRule.onAllNodes(hasTestTag("contactItem"))[0].assertExists()

        // composeTestRule.onNodeWithText("Remove").assertExists()
        // composeTestRule.onNodeWithText("Connect").assertExists()
        // composeTestRule.onNodeWithText("Received 6/13/2025").assertExists()
        // composeTestRule.onNodeWithContentDescription("User search").assertExists()
        // composeTestRule.onNodeWithContentDescription("User avatar").assertExists()
    }
}
