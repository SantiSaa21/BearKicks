package com.bearkicks.app.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.usecase.GetCurrentUserUseCase
import com.bearkicks.app.features.auth.domain.usecase.LogoutUseCase
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfileUseCase
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfilePhotoUseCase
import com.bearkicks.app.features.auth.domain.usecase.ChangeProfilePhotoUseCase
import com.bearkicks.app.features.auth.domain.usecase.ChangePasswordUseCase
import android.net.Uri
import com.bearkicks.app.features.cart.domain.usecase.ClearOrdersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Authenticated(val user: UserModel) : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    observeAuth: ObserveAuthStateUseCase,
    private val getCurrent: GetCurrentUserUseCase,
    private val logout: LogoutUseCase,
    private val clearOrders: ClearOrdersUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val updatePhoto: UpdateProfilePhotoUseCase,
    private val changePhoto: ChangeProfilePhotoUseCase,
    private val changePassword: ChangePasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            observeAuth().collect { user ->
                _state.value = when (user) {
                    null -> ProfileUiState.LoggedOut
                    else -> ProfileUiState.Authenticated(user)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrent()
            _state.value = when (user) {
                null -> ProfileUiState.LoggedOut
                else -> ProfileUiState.Authenticated(user)
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            logout()
        }
    }

    fun onClearOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            clearOrders()
        }
    }

    fun onSaveProfile(firstName: String?, lastName: String?, phone: String?, address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = updateProfile(firstName, lastName, phone, address)
            result.onSuccess { _state.value = ProfileUiState.Authenticated(it) }
                .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Error al actualizar perfil") }
        }
    }

    fun onChangePhotoUrl(photoPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = updatePhoto(photoPath)
            result.onSuccess { _state.value = ProfileUiState.Authenticated(it) }
                .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Error al actualizar foto") }
        }
    }

    fun onPickPhoto(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = changePhoto(uri)
            result.onSuccess { _state.value = ProfileUiState.Authenticated(it) }
                .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Error al subir foto") }
        }
    }

    fun onChangePassword(current: String, new: String, confirm: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val r = changePassword(current, new, confirm)
            onResult(r)
        }
    }

    fun onVerifyCurrentPassword(current: String, onResult: (Boolean, String?) -> Unit) {
        val authUser = FirebaseAuth.getInstance().currentUser
        val email = authUser?.email
        if (authUser == null || email == null) {
            onResult(false, "No hay sesión activa")
            return
        }
        val credential = EmailAuthProvider.getCredential(email, current)
        authUser.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                val raw = task.exception?.message ?: "Contraseña actual incorrecta"
                val mapped = when {
                    raw.contains("auth credential", ignoreCase = true) -> "La contraseña actual es incorrecta o la sesión expiró"
                    raw.contains("invalid", ignoreCase = true) -> "Credenciales inválidas"
                    raw.contains("expired", ignoreCase = true) -> "La sesión expiró. Inicia sesión nuevamente."
                    else -> raw
                }
                onResult(false, mapped)
            }
        }
    }
}
