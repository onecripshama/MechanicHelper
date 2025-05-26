package com.example.mechanichelper.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.data.api.User
import com.example.mechanichelper.data.api.UsersApi
import com.example.mechanichelper.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersApi: UsersApi,
    private val prefs: PreferencesManager
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _hasError = mutableStateOf(false)
    val hasError: State<Boolean> = _hasError

    private val _hasSearched = mutableStateOf(false)
    val hasSearched: State<Boolean> = _hasSearched

    private val _showNoResults = mutableStateOf(false)
    val showNoResults: State<Boolean> = _showNoResults

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _searchHistory = mutableStateOf<List<String>>(emptyList())
    val searchHistory: List<String> get() = _searchHistory.value

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun search() {
        _hasSearched.value = true
        _showNoResults.value = false

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val response = if (_searchQuery.value.isNotBlank()) {
                    usersApi.searchUsers(_searchQuery.value).also {
                        addToHistory(_searchQuery.value)
                    }
                } else {
                    usersApi.getAllUsers()
                }
                _users.value = response.users
                _hasError.value = false
            } catch (e: Exception) {
                _users.value = emptyList()
                _hasError.value = true
            } finally {
                _isSearching.value = false
                _showNoResults.value = _users.value.isEmpty() && !_hasError.value
            }
        }
    }

    private fun addToHistory(query: String) {
        if (query.isBlank()) return
        val newHistory = _searchHistory.value.toMutableList().apply {
            remove(query)
            add(0, query)
            if (size > 5) removeAt(size - 1)
        }
        _searchHistory.value = newHistory
        prefs.saveSearchHistory(newHistory)
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            prefs.saveSearchHistory(emptyList())
        }
    }

    fun clear() {
        _searchQuery.value = ""
        _hasSearched.value = false
        _showNoResults.value = false
    }
}