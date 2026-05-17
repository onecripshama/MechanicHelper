package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.UserProfileUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val carRepository: CarRepository,
    private val remindersRepository: RemindersRepository
) : ViewModel() {
    val profile: StateFlow<UserProfileUi> = userPreferencesRepository.profile

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.clearSession()
            carRepository.refresh()
            remindersRepository.refresh()
            onLoggedOut()
        }
    }
}
