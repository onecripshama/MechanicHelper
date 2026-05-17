package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.ApiClient
import com.example.mechanichelper.auth.AuthErrorMapper
import com.example.mechanichelper.auth.AuthResponse
import com.example.mechanichelper.auth.RegisterRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val loginError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiEvent {
        object RegistrationSuccess : UiEvent()
    }

    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow: Flow<UiEvent> = _eventChannel.receiveAsFlow()

    fun clearErrors() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                loginError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null
            )
        }
    }

    fun register(login: String, password: String, email: String, confirmPassword: String) {
        val loginError = when {
            login.isBlank() -> "Введите логин"
            login.length < 3 -> "Логин должен быть не короче 3 символов"
            else -> null
        }
        val emailError = when {
            email.isBlank() -> "Введите email"
            !EMAIL_REGEX.matches(email) -> "Некорректный формат email"
            else -> null
        }
        val passwordError = when {
            password.isBlank() -> "Введите пароль"
            password.length < 6 -> "Пароль должен быть не короче 6 символов"
            else -> null
        }
        val confirmPasswordError = when {
            confirmPassword.isBlank() -> "Подтвердите пароль"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }

        if (loginError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.value = UiState(
                loginError = loginError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }

        _uiState.value = UiState(isLoading = true)

        ApiClient.authApi.register(RegisterRequest(login = login, password = password, email = email))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        _uiState.value = UiState(isLoading = false)
                        viewModelScope.launch {
                            _eventChannel.send(UiEvent.RegistrationSuccess)
                        }
                    } else {
                        val msg = AuthErrorMapper.fromHttp(response, isLogin = false)
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

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
