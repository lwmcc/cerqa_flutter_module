package com.mccartycarclub.repository

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    @Json(name = "userId") val userId: String,
    val firstName: String,
    val lastName: String,
    val name: String,
    val phone: String,
    @Json(name = "userName") val userName: String,
    val email: String,
    @Json(name = "avatarUri") val avatarUri: String,
    @Json(name = "createdAt") val createdAt: DateTime,
    @Json(name = "updatedAt") val updatedAt: DateTime
)

@JsonClass(generateAdapter = true)
data class DateTime(
    @Json(name = "offsetDateTime") val offsetDateTime: String
)

@JsonClass(generateAdapter = true)
data class Success(
    val data: User
)
