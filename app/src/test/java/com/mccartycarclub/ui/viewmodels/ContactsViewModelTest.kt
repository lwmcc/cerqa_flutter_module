package com.mccartycarclub.ui.viewmodels

import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.helpers.LocalDeviceContacts
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.model.SearchContact
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.repository.AmplifyRepo.Companion.DUMMY
import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.ContactsWrapper
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.NetDeleteResult
import com.mccartycarclub.repository.NetSearchResult
import com.mccartycarclub.repository.NetWorkResult
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.testdoubles.receivedInvites
import com.mccartycarclub.ui.components.ContactCardEvent
import com.mccartycarclub.ui.shared.MessageTypes
import com.mccartycarclub.viewmodels.ContactsViewModel
import io.ably.lib.rest.Auth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ContactsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var contactsViewModel: ContactsViewModel

    @Test
    fun `assert user data is correct on success`() = runTest {
        contactsViewModel = ContactsViewModel(
            repo = FakeRemoteRepo(
                response = NetworkResponse.Success(receivedInvites)
            ),
            contactsRepository = FakeContactsRepository(),
            localRepo = FakeLocalRepo(),
        )
        contactsViewModel.fetchAllContacts(DUMMY)

        val userName = contactsViewModel.uiState.contacts[0].userName
        val contactId = contactsViewModel.uiState.contacts[0].contactId
        val userId = contactsViewModel.uiState.contacts[0].userId
        val name = contactsViewModel.uiState.contacts[0].name
        val avatarUri = contactsViewModel.uiState.contacts[0].avatarUri
        val createdAt = contactsViewModel.uiState.contacts[0].createdAt

        assertEquals(userName, "LarryM")
        assertEquals(contactId, "1212-2222-3344")
        assertEquals(userId, "2222-1122-3344")
        assertEquals(name, "Larry")
        assertEquals(avatarUri, "https://www.cerqa.app/avatars/larrym")
        assertEquals(createdAt, "6/13/2025")
    }

    @Test
    fun `assert message type NoInternet when no internet`() = runTest {
        contactsViewModel = ContactsViewModel(
            repo = FakeRemoteRepo(
                response = NetworkResponse.NoInternet
            ),
            contactsRepository = FakeContactsRepository(),
            localRepo = FakeLocalRepo()
        )

        contactsViewModel.fetchAllContacts(DUMMY)

        assertEquals(MessageTypes.NoInternet, contactsViewModel.uiState.message)
    }

    @Test
    fun `assert message type null when no message`() = runTest {
        contactsViewModel = ContactsViewModel(
            repo = FakeRemoteRepo(),
            contactsRepository = FakeContactsRepository(),
            localRepo = FakeLocalRepo(),
        )

        contactsViewModel.fetchAllContacts(DUMMY)
        assertNull(contactsViewModel.uiState.message)
    }

    @Test
    fun `assert message type error when error message`() = runTest {
        contactsViewModel = ContactsViewModel(
            repo = FakeRemoteRepo(
                response = NetworkResponse.Error(IOException("An Error Occurred"))
            ),
            contactsRepository = FakeContactsRepository(),
            localRepo = FakeLocalRepo(),
        )

        contactsViewModel.fetchAllContacts(DUMMY)
        assertEquals(MessageTypes.Error, contactsViewModel.uiState.message)
    }

/*    @Test
    fun `assert invite sent true when network response success`() = runTest {
        contactsViewModel = ContactsViewModel(
            repo = FakeRemoteRepo(),
            contactsRepository = FakeContactsRepository(),
            localRepo = FakeLocalRepo(),
        )

        contactsViewModel.userConnectionEvent(
            connectionEvent = ContactCardEvent.InviteConnectEvent(
                DUMMY,
                DUMMY,
            )
        )

        assertTrue(contactsViewModel.inviteSentSuccess.value)
    }*/

}

class FakeRemoteRepo(
    var response: NetworkResponse<List<Contact>> = NetworkResponse.Success(
        receivedInvites
    )
) : RemoteRepo {
    override fun contactExists(
        senderUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun hasExistingInviteToAcceptOrReject(
        loggedInUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun fetchUserByUserName(userName: String): Flow<NetSearchResult<User?>> {
        TODO("Not yet implemented")
    }

    override fun sendInviteToConnect(
        receiverUserId: String,
        rowId: String
    ): Flow<NetworkResponse<String>> = flow {
        emit(NetworkResponse.Success("24344c99-558d-4788-8659-5752e3656c70"))
    }

    override fun sendPhoneNumberInviteToConnect(phoneNumber: String): Flow<NetworkResponse<String>> {
        TODO("Not yet implemented")
    }

    override fun cancelInviteToConnect(
        senderUserId: String,
        receiverUserId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    override fun deleteContact(
        loggedInUserId: String,
        contactId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    override fun deleteReceivedInviteToContact(
        loggedInUserId: String,
        contactId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    override suspend fun createContact(user: User) {
        TODO("Not yet implemented")
    }

    override fun fetchReceivedInvites(): Flow<NetWorkResult<List<Contact>>> {
        TODO("Not yet implemented")
    }

    override fun fetchSentInvites(): Flow<NetWorkResult<List<Contact>>> {
        TODO("Not yet implemented")
    }

    override fun fetchAllContacts(): Flow<NetworkResponse<List<Contact>>> =
        flow {
            emit(response)
        }

    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    override fun fetchAblyToken(userId: String): Flow<Auth.TokenRequest> {
        TODO("Not yet implemented")
    }

    override fun searchUsers(
        loggedInUserId: String?,
        userName: String
    ): Flow<NetworkResponse<UserSearchResult>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchUsersByUserName(userName: String): Flow<NetworkResponse<List<User>>> {
        TODO("Not yet implemented")
    }

}

class FakeLocalRepo : LocalRepository {
    override suspend fun setLocalUserId(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getUserId(): Flow<String?> = flow {
        emit(DUMMY)
    }

    override suspend fun getAllContacts(): List<LocalDeviceContacts> {
        TODO("Not yet implemented")
    }
}

class FakeContactsRepository : ContactsRepository {
    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<NetDeleteResult> {
        TODO("Not yet implemented")
    }

    override fun contactExists(
        senderUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override fun fetchAllContacts(): Flow<NetworkResponse<List<Contact>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUsersByPhoneNumber(): Pair<List<SearchContact>, List<SearchContact>> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeviceContacts(): ContactsWrapper {
        TODO("Not yet implemented")
    }

    override suspend fun createContact(user: User) {
        TODO("Not yet implemented")
    }

}
