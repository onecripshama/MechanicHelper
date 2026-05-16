package com.example.mechanichelper.domain

import kotlinx.coroutines.flow.StateFlow

data class UserProfileUi(val login: String = "")

interface UserPreferencesRepository {
    suspend fun saveUserLogin(login: String)
    val profile: StateFlow<UserProfileUi>
}
