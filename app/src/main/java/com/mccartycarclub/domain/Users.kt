package com.mccartycarclub.domain
data class Contact(
    val userId: String
)

data class Group(
    // val id: String,
    val isAdmin: Boolean,
    val members: List<Member>,
)

data class Member(
    val id: String,
    val userId: String,
)

data class Vehicle(
    val id: String,
    val carMake: String,
    val carModel: String,
    val year: Int,
)
