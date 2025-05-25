package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.ApiClient
import com.example.mechanichelper.auth.AuthResponse
import com.example.mechanichelper.auth.RegisterRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiEvent {
        object RegistrationSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }

    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow: Flow<UiEvent> = _eventChannel.receiveAsFlow()

    fun register(login: String, password: String, email: String) {
        _uiState.value = UiState(isLoading = true, errorMessage = null)

        val call = ApiClient.authApi.register(
            RegisterRequest(login = login, password = password, email = email)
        )

        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _uiState.value = UiState(isLoading = false)

                if (response.isSuccessful && response.body() != null) {
                    viewModelScope.launch {
                        _eventChannel.send(UiEvent.RegistrationSuccess)
                    }
                } else {
                    val msg = "Ошибка регистрации: код ${response.code()}"
                    _uiState.value = UiState(errorMessage = msg)
                    viewModelScope.launch {
                        _eventChannel.send(UiEvent.ShowError(msg))
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                val msg = "Ошибка сети: ${t.localizedMessage ?: "unknown"}"
                _uiState.value = UiState(isLoading = false, errorMessage = msg)
                viewModelScope.launch {
                    _eventChannel.send(UiEvent.ShowError(msg))
                }
            }
        })
    }
}