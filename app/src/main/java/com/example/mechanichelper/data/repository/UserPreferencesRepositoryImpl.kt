package com.example.mechanichelper.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.UserProfileUi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "user_prefs"
private const val KEY_LOGIN = "user_login"
private const val KEY_TOKEN = "auth_token"

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val savedLogin = prefs.getString(KEY_LOGIN, "").orEmpty()
    private val savedToken = prefs.getString(KEY_TOKEN, "").orEmpty()

    private val _profile = MutableStateFlow(UserProfileUi(savedLogin))
    override val profile: StateFlow<UserProfileUi> = _profile.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(savedToken.isNotBlank())
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    override suspend fun saveSession(login: String, token: String) = withContext(Dispatchers.IO) {
        prefs.edit {
            putString(KEY_LOGIN, login)
            putString(KEY_TOKEN, token)
        }
        _profile.value = UserProfileUi(login)
        _isLoggedIn.value = true
    }

    override suspend fun clearSession() = withContext(Dispatchers.IO) {
        prefs.edit {
            remove(KEY_LOGIN)
            remove(KEY_TOKEN)
        }
        _profile.value = UserProfileUi()
        _isLoggedIn.value = false
    }

    override fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    override fun getCurrentLogin(): String? = _profile.value.login.takeIf { it.isNotBlank() }
}
