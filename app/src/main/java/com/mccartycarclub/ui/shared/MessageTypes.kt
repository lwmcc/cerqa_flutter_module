package com.mccartycarclub.ui.shared

sealed class MessageTypes {
    data object NoInternet : MessageTypes()
    data object Error : MessageTypes()
}
