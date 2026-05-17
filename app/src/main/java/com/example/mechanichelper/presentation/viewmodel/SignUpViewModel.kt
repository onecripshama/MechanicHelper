package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.AuthApi
import com.example.mechanichelper.auth.AuthErrorMapper
import com.example.mechanichelper.auth.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authApi: AuthApi
) : ViewModel() {

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

        viewModelScope.launch {
            try {
                authApi.register(RegisterRequest(login = login, password = password, email = email))
                _uiState.value = UiState(isLoading = false)
                _eventChannel.send(UiEvent.RegistrationSuccess)
            } catch (e: HttpException) {
                val response = e.response()
                val msg = if (response != null) {
                    AuthErrorMapper.fromHttp(response, isLogin = false)
                } else {
                    "Не удалось зарегистрироваться"
                }
                _uiState.value = UiState(isLoading = false, errorMessage = msg)
            } catch (t: Throwable) {
                _uiState.value = UiState(
                    isLoading = false,
                    errorMessage = AuthErrorMapper.fromThrowable(t)
                )
            }
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
