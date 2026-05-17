package com.example.mechanichelper.domain

import kotlinx.coroutines.flow.StateFlow

data class UserProfileUi(val login: String = "")

interface UserPreferencesRepository {
    val profile: StateFlow<UserProfileUi>
    val isLoggedIn: StateFlow<Boolean>

    suspend fun saveSession(login: String, token: String)
    suspend fun clearSession()
    fun getAuthToken(): String?
}
