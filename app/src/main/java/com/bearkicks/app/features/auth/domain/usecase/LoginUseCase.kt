package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.vo.Email
import com.bearkicks.app.features.auth.domain.model.vo.Password
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository

class LoginUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        Email.create(email).flatMap { _ -> Password.create(password) }
            .fold(
                onSuccess = { repo.login(email.trim(), password.trim()) },
                onFailure = { Result.failure(it) }
            )
}

private inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(onSuccess = transform, onFailure = { Result.failure(it) })
