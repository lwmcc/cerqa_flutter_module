package com.mccartycarclub.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class Invite(
    val id: String,
    val firstName: String,
    val lastName: String
)

data class InviteToConnect(
    val id: String,
    val senderUserId: String,
    val receiverUserId: String,
    val invites: Invite
)

data class ListInviteToConnects(
    val items: List<InviteToConnect>
)

data class ApiResponse(
    @Json(name = "listInviteToConnects") val listInviteToConnects: ListInviteToConnects
)

fun parseJson(json: String): ApiResponse? {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter: JsonAdapter<ApiResponse> = moshi.adapter(ApiResponse::class.java)
    return jsonAdapter.fromJson(json)
}
