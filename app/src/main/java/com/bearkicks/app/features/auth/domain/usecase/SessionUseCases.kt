package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(private val repo: IAuthRepository) {
    operator fun invoke(): Flow<UserModel?> = repo.observeUser()
}

class GetCurrentUserUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(): UserModel? = repo.getCurrentUser()
}

class LogoutUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
