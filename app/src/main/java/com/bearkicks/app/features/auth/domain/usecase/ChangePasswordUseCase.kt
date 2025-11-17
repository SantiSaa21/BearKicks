package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.vo.Password
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class ChangePasswordUseCase(private val auth: FirebaseAuth) {
    suspend operator fun invoke(currentPassword: String, newPassword: String, confirmPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(IllegalStateException("No autenticado"))
        val email = user.email ?: return Result.failure(IllegalStateException("Usuario sin email"))

        if (newPassword != confirmPassword) {
            return Result.failure(IllegalArgumentException("Las contraseñas no coinciden"))
        }

        val vo = Password.create(newPassword)
        if (vo.isFailure) return Result.failure(vo.exceptionOrNull()!!)

        return try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await() // asegura sesión reciente
            user.updatePassword(vo.getOrThrow().value).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("INVALID_LOGIN_CREDENTIALS", true) == true -> "Contraseña actual incorrecta"
                e.message?.contains("TOO_MANY_ATTEMPTS", true) == true -> "Demasiados intentos, espera unos minutos"
                else -> e.message ?: "Error al cambiar contraseña"
            }
            Result.failure(IllegalStateException(msg, e))
        }
    }
}
