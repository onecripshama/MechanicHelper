package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.UserProfileUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val profile: StateFlow<UserProfileUi> = userPreferencesRepository.profile
}
