package com.cerqa.data

// Stub data class until GetUserByUserIdQuery is available
data class UserData(
    val id: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val avatarUri: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val name: String? = null,
    val phone: String? = null
)

interface UserRepository {
    suspend fun getUser(): Result<UserData>
}
