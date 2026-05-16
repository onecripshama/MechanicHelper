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

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(UserProfileUi(prefs.getString(KEY_LOGIN, "") ?: ""))
    override val profile: StateFlow<UserProfileUi> = _profile.asStateFlow()

    override suspend fun saveUserLogin(login: String) = withContext(Dispatchers.IO) {
        prefs.edit { putString(KEY_LOGIN, login) }
        _profile.value = UserProfileUi(login)
    }
}
