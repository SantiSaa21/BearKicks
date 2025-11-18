package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.vo.Password
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

class ChangePasswordUseCase(private val auth: FirebaseAuth) {
    suspend operator fun invoke(currentPassword: String, newPassword: String, confirmPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(DomainException(ErrorKey.NOT_AUTHENTICATED))
        val email = user.email ?: return Result.failure(DomainException(ErrorKey.USER_MISSING_EMAIL))

        if (newPassword != confirmPassword) return Result.failure(DomainException(ErrorKey.PASSWORDS_DO_NOT_MATCH))

        val vo = Password.create(newPassword)
        if (vo.isFailure) return Result.failure(vo.exceptionOrNull()!!)

        return try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await() // asegura sesión reciente
            user.updatePassword(vo.getOrThrow().value).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val key = when {
                e.message?.contains("INVALID_LOGIN_CREDENTIALS", true) == true -> ErrorKey.WRONG_CURRENT_PASSWORD
                e.message?.contains("TOO_MANY_ATTEMPTS", true) == true -> ErrorKey.TOO_MANY_ATTEMPTS
                else -> ErrorKey.CHANGE_PASSWORD_ERROR
            }
            Result.failure(DomainException(key, e))
        }
    }
}
