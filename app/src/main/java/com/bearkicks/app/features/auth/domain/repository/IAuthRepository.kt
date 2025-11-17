package com.bearkicks.app.features.auth.domain.repository

import com.bearkicks.app.features.auth.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<UserModel>
    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String?,
        address: String?,
        birthDate: Long,
        password: String,
        photoPath: String? = null
    ): Result<UserModel>

    fun observeUser(): Flow<UserModel?>
    suspend fun getCurrentUser(): UserModel?
    suspend fun logout(): Result<Unit>

    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        address: String? = null,
        photoPath: String? = null
    ): Result<UserModel>
}
