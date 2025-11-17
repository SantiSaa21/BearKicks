package com.bearkicks.app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    data object Idle : RegisterUiState()
    data object Loading : RegisterUiState()
    data class Success(val user: UserModel) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(private val register: RegisterUseCase) : ViewModel() {
    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onRegister(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String?,
        address: String?,
        birthDate: Long,
        password: String,
        photoPath: String?
    ) {
        _state.value = RegisterUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val res = register(firstName, lastName, username, email, phone, address, birthDate, password, photoPath)
            _state.value = res.fold(
                onSuccess = { RegisterUiState.Success(it) },
                onFailure = { RegisterUiState.Error(it.message ?: "Error al registrar") }
            )
        }
    }
}
