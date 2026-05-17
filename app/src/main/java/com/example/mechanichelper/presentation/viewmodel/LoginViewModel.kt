package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.auth.AuthApi
import com.example.mechanichelper.auth.AuthErrorMapper
import com.example.mechanichelper.auth.LoginRequest
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: UserPreferencesRepository,
    private val authApi: AuthApi,
    private val carRepository: CarRepository,
    private val remindersRepository: RemindersRepository
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

        viewModelScope.launch {
            try {
                val response = authApi.login(LoginRequest(login, password))
                repo.saveSession(login, response.token)
                carRepository.refresh()
                remindersRepository.refresh()
                _uiState.value = UiState(isLoading = false)
                _events.send(UiEvent.LoginSuccess)
            } catch (e: HttpException) {
                val response = e.response()
                val msg = if (response != null) {
                    AuthErrorMapper.fromHttp(response, isLogin = true)
                } else {
                    "Не удалось войти"
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
}
