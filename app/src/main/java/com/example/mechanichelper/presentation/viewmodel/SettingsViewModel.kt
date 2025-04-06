package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val partsViewModel: PartsViewModel
) : ViewModel() {

    fun clearSearchHistory() {
        partsViewModel.clearSearchHistory()
    }

    private val _isDarkTheme = MutableStateFlow(preferencesManager.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        viewModelScope.launch(Dispatchers.IO) {
            preferencesManager.setDarkTheme(enabled)
        }
    }
}
