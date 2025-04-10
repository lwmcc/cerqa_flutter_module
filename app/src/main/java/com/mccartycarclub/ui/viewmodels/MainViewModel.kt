package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.domain.model.LocalContact
import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.navigation.ClickNavigation
import com.mccartycarclub.repository.AmplifyDbRepo
import com.mccartycarclub.repository.NetResult
import com.mccartycarclub.utils.fetchUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val user: GetUser,  // TODO: edit these names to use usecase
    private val userContacts: GetContacts,
    private val dbRepo: AmplifyDbRepo,
) : ViewModel() {

    private val _hasPendingInvite = MutableStateFlow(false)
    val hasPendingInvite = _hasPendingInvite.asStateFlow()

    private val _hasConnection = MutableStateFlow(false)
    val hasConnection = _hasConnection.asStateFlow()

    private val _localContacts = MutableStateFlow(emptyList<LocalContact>())
    val localContacts = _localContacts.asStateFlow()

    private val _query = MutableStateFlow("")
    val searchResults: StateFlow<NetResult<User?>> = _query
        .debounce(1000)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { userName ->
            callbackFlow {
                dbRepo.fetchUserByUserName(
                    userName = userName,
                    data = { data ->
                        when (data) {
                            NetResult.Pending -> {

                            }

                            is NetResult.Success -> {
                                trySend(data)
                                fetchUserId { loggedIn ->
                                    if (loggedIn.loggedIn) {
                                        loggedIn.userId?.let { userId ->
                                            dbRepo.hasExistingInvite(
                                                senderUserId = userId,
                                                receiverUserId = data.data?.userId.toString(), // TODO: do right was
                                                hasInvite = { hasPendingInvite ->
                                                    _hasPendingInvite.value = hasPendingInvite
                                                })

                                            dbRepo.contactExists(
                                                senderUserId = userId,
                                                receiverUserId = data.data?.userId.toString(),
                                                hasConnection = { hasConnection ->
                                                    println("MainViewModel ***** CONNECTION $hasConnection")
                                                    _hasConnection.value = hasConnection
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            is NetResult.Error -> {

                            }
                        }
                    }
                )

                awaitClose {
                    // TODO: do I need this?
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NetResult.Pending
        )

    init {
        user.getUser(TEST_USER_1,
            user = {
               // dbRepo.createContact(it)
            })

//        user.getUserGroups(TEST_USER_1)
//
//        userContacts.fetchContacts(TEST_USER_1, userContacts = {
//            println("MainViewModel ***** CONTACTS $it")
//        })
//
        //userContacts.getUserContacts(TEST_USER_1)
        // TODO: get all contacts
    }

    companion object {
        const val TEST_USER_1 = "14f8f4e8-a0b1-7015-d3b4-ded92d05abe5"
        const val TEST_USER_2 = "c468f4b8-3021-70f1-7f5b-2b7aef73da45"
    }

    fun getDeviceContacts() = userContacts.getDeviceContacts(localContacts = { contacts ->
        _localContacts.update { contacts }
    })

    fun inviteContact(userId: String, rowId: (String) -> Unit) {
        userContacts.addNewContact(userId, rowId = {
            it?.let {
                rowId(it)
            }
        })
    }

    fun fetchUserIdFromSentInvite(rowId: String) {
        user.fetchUserIdFromSentInvite(rowId, userId = {
            println("MainViewModel ***** USER ID $it")
        })
    }

    fun acceptContactInvite() {
        userContacts.acceptContactInvite()
    }

    fun fetchUserContacts(userId: String) {
        userContacts.getUserContacts(userId)
    }

    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
    }

    fun createConnectInvite(receiverUserId: String?) {
        // TODO: make reusable
        fetchUserId { loggedIn ->
            if (loggedIn.loggedIn) {
                loggedIn.userId?.let { userId ->
                    dbRepo.hasExistingInvite(
                        senderUserId = userId,
                        receiverUserId = receiverUserId.toString(), // TODO: do right was
                        hasInvite = { hasPendingInvite ->
                            _hasPendingInvite.value = hasPendingInvite
                        })

                    dbRepo.contactExists(
                        senderUserId = userId,
                        receiverUserId = receiverUserId.toString(),
                        hasConnection = { hasConnection ->
                            println("MainViewModel ***** CONNECTION $hasConnection")
                            _hasConnection.value = hasConnection
                        }
                    )
                }
            }
        }
    }



}