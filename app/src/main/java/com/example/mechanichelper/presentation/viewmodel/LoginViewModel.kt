package com.example.mechanichelper.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.ApiClient
import com.example.mechanichelper.auth.AuthResponse
import com.example.mechanichelper.auth.LoginRequest
import com.example.mechanichelper.domain.UserPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = UserPreferencesRepository(application)

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiEvent {
        object LoginSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow: Flow<UiEvent> = _events.receiveAsFlow()

    fun login(login: String, password: String) {
        _uiState.value = UiState(isLoading = true, errorMessage = null)

        ApiClient.authApi.login(LoginRequest(login, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    _uiState.value = UiState(isLoading = false)
                    if (response.isSuccessful && response.body() != null) {
                        viewModelScope.launch {
                            repo.saveUserLogin(login)
                            _events.send(UiEvent.LoginSuccess)
                        }
                    } else {
                        val msg = "Ошибка входа: код ${response.code()}"
                        _uiState.value = UiState(errorMessage = msg)
                        viewModelScope.launch { _events.send(UiEvent.ShowError(msg)) }
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    val msg = "Ошибка сети: ${t.localizedMessage ?: "unknown"}"
                    _uiState.value = UiState(isLoading = false, errorMessage = msg)
                    viewModelScope.launch { _events.send(UiEvent.ShowError(msg)) }
                }
            })
    }
}