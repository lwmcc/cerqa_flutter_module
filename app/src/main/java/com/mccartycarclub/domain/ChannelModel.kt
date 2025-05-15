package com.mccartycarclub.domain

sealed class ChannelModel(private val prefix: String) {
    data object NotificationsInvitations : ChannelModel("notifications:invitations:")

    fun getName(userId: String) = "$prefix$userId"
}