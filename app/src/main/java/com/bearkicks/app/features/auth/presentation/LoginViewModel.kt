package com.bearkicks.app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: UserModel) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val login: LoginUseCase) : ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onLogin(email: String, password: String) {
        _state.value = LoginUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val res = login(email, password)
            _state.value = res.fold(
                onSuccess = { LoginUiState.Success(it) },
                onFailure = { LoginUiState.Error(mapAuthError(it)) }
            )
        }
    }
}

private fun mapAuthError(t: Throwable): String {
    // Firebase exceptions específicas
    return when (t) {
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo."
        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas. Verifica tu correo y contraseña."
        is FirebaseAuthException -> when (t.errorCode) {
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
            "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta."
            "ERROR_INVALID_EMAIL" -> "Formato de correo inválido."
            "ERROR_USER_DISABLED" -> "La cuenta está deshabilitada."
            else -> "Error de autenticación: ${t.errorCode?.lowercase()?.replace('_',' ')}"
        }
        else -> t.message ?: "Error al iniciar sesión"
    }
}
