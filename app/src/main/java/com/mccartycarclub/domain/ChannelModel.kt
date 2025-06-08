package com.mccartycarclub.domain

sealed class ChannelModel(private val prefix: String) {
    data object NotificationsDirect : ChannelModel("private:notifications:users:")

    fun getName(userId: String) = "$prefix$userId"
}
