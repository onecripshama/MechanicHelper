package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.ApiClient
import com.example.mechanichelper.auth.AuthErrorMapper
import com.example.mechanichelper.auth.AuthResponse
import com.example.mechanichelper.auth.LoginRequest
import com.example.mechanichelper.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserPreferencesRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val loginError: String? = null,
        val passwordError: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiEvent {
        object LoginSuccess : UiEvent()
    }

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow: Flow<UiEvent> = _events.receiveAsFlow()

    fun clearErrors() {
        _uiState.update { it.copy(errorMessage = null, loginError = null, passwordError = null) }
    }

    fun login(login: String, password: String) {
        val loginError = if (login.isBlank()) "Введите логин" else null
        val passwordError = if (password.isBlank()) "Введите пароль" else null
        if (loginError != null || passwordError != null) {
            _uiState.value = UiState(loginError = loginError, passwordError = passwordError)
            return
        }

        _uiState.value = UiState(isLoading = true)

        ApiClient.authApi.login(LoginRequest(login, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = UiState(isLoading = false)
                        viewModelScope.launch {
                            repo.saveUserLogin(login)
                            _events.send(UiEvent.LoginSuccess)
                        }
                    } else {
                        val msg = AuthErrorMapper.fromHttp(response, isLogin = true)
                        _uiState.value = UiState(isLoading = false, errorMessage = msg)
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    _uiState.value = UiState(
                        isLoading = false,
                        errorMessage = AuthErrorMapper.fromThrowable(t)
                    )
                }
            })
    }
}
