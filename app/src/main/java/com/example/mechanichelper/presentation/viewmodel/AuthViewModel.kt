package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mechanichelper.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = userPreferencesRepository.isLoggedIn
}
