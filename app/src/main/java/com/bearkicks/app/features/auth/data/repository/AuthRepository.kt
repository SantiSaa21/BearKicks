package com.bearkicks.app.features.auth.data.repository

import com.bearkicks.app.features.auth.data.datastore.AuthDataStore
import com.bearkicks.app.features.auth.data.datasource.AuthFirebaseDataSource
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AuthRepository(
    private val ds: AuthFirebaseDataSource,
    private val store: AuthDataStore
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<UserModel> = runCatching {
        val user = ds.login(email, password)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoPath)
        user
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String?,
        address: String?,
        birthDate: Long,
        password: String,
        photoPath: String?
    ): Result<UserModel> = runCatching {
        val user = ds.register(firstName, lastName, username, email, phone, address, birthDate, password, photoPath)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoPath)
        user
    }

    override fun observeUser(): Flow<UserModel?> = ds.observeUser().onEach { user ->
        if (user == null) store.clear() else store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoPath)
    }

    override suspend fun getCurrentUser(): UserModel? = ds.getCurrentUser()
    override suspend fun logout(): Result<Unit> = runCatching { ds.logout(); store.clear() }

    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?,
        photoPath: String?
    ): Result<UserModel> = runCatching {
        val user = ds.updateProfile(firstName, lastName, phone, address, photoPath)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoPath)
        user
    }
}
