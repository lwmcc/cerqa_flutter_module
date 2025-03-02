package com.mccartycarclub.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.mccartycarclub.domain.usecases.user.GetUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val user: GetUser) : ViewModel() {

    init {
        user.getUserGroups("31cb55f0-1031-7026-1ea5-9e5c424b27de")
    }
}