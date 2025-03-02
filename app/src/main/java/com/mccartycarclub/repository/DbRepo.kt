package com.mccartycarclub.repository

interface DbRepo {
    fun fetchUserGroups(userId: String)
}