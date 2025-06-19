package com.mccartycarclub.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.repository.LocalRepo
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.ui.components.ContactCardConnectionEvent
import com.mccartycarclub.ui.viewmodels.ContactsViewModel.Companion.SEARCH_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class SearchUiState(
    val pending: Boolean = false,
    val idle: Boolean = true,
    val isSendingInvite: Boolean = false,
    val inviteSent: Boolean = false,
    val message: String? = null,
    val searchResult: UserSearchResult? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: RemoteRepo,
    private val localRepo: LocalRepo,
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _query = MutableStateFlow<String?>(null)
    val query = _query.asStateFlow()

    var uiState by mutableStateOf(SearchUiState())
        private set

    init {
        viewModelScope.launch {
            _userId.value = localRepo.getUserId().first()

            query.debounce(SEARCH_DELAY.milliseconds).distinctUntilChanged()
                .collectLatest { userName ->

                    if (!userName.isNullOrEmpty()) {
                        uiState = uiState.copy(pending = true)
                        repo.searchUsers(_userId.value, userName).collect { response ->
                            uiState = when (response) {
                                is NetworkResponse.Error -> {
                                    // TODO: move messages to enum
                                    uiState.copy(message = "An Error Occurred")
                                }

                                NetworkResponse.NoInternet -> {
                                    // TODO: move messages to enum
                                    uiState.copy(message = "No Internet")
                                }

                                is NetworkResponse.Success -> {
                                    uiState.copy(
                                        searchResult = response.data,
                                        pending = false,
                                        inviteSent = false,
                                        isSendingInvite = false,
                                    )
                                }
                            }
                        }
                    } else {
                        uiState = uiState.copy(idle = true)
                    }
                }
        }
    }

    fun inviteSentEvent(connectionEvent: ContactCardConnectionEvent) {

        when (connectionEvent) {
            is ContactCardConnectionEvent.InviteConnectEvent -> {
                viewModelScope.launch {
                    uiState = uiState.copy(pending = true, isSendingInvite = true)
                    //val channelName =
                    //    ChannelModel.NotificationsDirect.getName(connectionEvent.receiverUserId)
                    // TODO: will uncomment when ready
                    //realtimePublishRepo.publish(channelName)
                    //realTime.createReceiverInviteSubscription(_userId.value.toString(), channel)

                    val data = repo.sendInviteToConnect(
                        senderUserId = _userId.value,
                        receiverUserId = connectionEvent.receiverUserId,
                        rowId = connectionEvent.rowId,
                    ).first()

                    when (data) {
                        is NetworkResponse.Error -> {
                            // TODO: log update ui
                        }

                        NetworkResponse.NoInternet -> {
                            // TODO: update ui
                        }

                        is NetworkResponse.Success -> {
                            uiState = uiState.copy(pending = false, inviteSent = true)
                        }
                    }
                }
            }
        }
    }

    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
    }

}
