package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Test

private class FakeAuthRepository : IAuthRepository {
    private val state = MutableStateFlow<UserModel?>(null)
    var registered: UserModel? = null
    override suspend fun login(email: String, password: String): Result<UserModel> = Result.failure(UnsupportedOperationException())
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
    ): Result<UserModel> {
        registered = UserModel("uid", firstName, lastName, username, email, phone, address, birthDate, photoPath, System.currentTimeMillis(), System.currentTimeMillis())
        state.value = registered
        return Result.success(registered!!)
    }
    override fun observeUser(): Flow<UserModel?> = state
    override suspend fun getCurrentUser(): UserModel? = registered
    override suspend fun logout(): Result<Unit> = Result.success(Unit)
    override suspend fun updateProfile(firstName: String?, lastName: String?, phone: String?, address: String?, photoPath: String?): Result<UserModel> = Result.failure(UnsupportedOperationException())
}

class RegisterUseCaseTest {
    private val repo = FakeAuthRepository()
    private val useCase = RegisterUseCase(repo)

    @Test fun register_success() = runBlocking {
        val now = System.currentTimeMillis()
        val birth = now - (19L * 365L * 24L * 60L * 60L * 1000L)
        val result = useCase(
            firstName = "Carlos",
            lastName = "Perez",
            username = "carlos01",
            email = "carlos@example.com",
            phone = "69503355",
            address = "Av America 123 Cochabamba",
            birthDate = birth,
            password = "Password1",
            photoPath = null
        )
        assertTrue(result.isSuccess)
        assertEquals("Carlos", repo.registered?.firstName)
        assertEquals("Perez", repo.registered?.lastName)
    }

    @Test fun register_invalid_firstName() = runBlocking {
        val now = System.currentTimeMillis()
        val birth = now - (19L * 365L * 24L * 60L * 60L * 1000L)
        val result = useCase(
            firstName = "C", // invalid
            lastName = "Perez",
            username = "carlos01",
            email = "carlos@example.com",
            phone = "69503355",
            address = "Av America 123 Cochabamba",
            birthDate = birth,
            password = "Password1",
            photoPath = null
        )
        assertTrue(result.isFailure)
        assertEquals("El nombre debe tener solo letras (2-50) sin espacios", result.exceptionOrNull()?.message)
    }

    @Test fun register_invalid_birthDate() = runBlocking {
        val now = System.currentTimeMillis()
        val under18 = now - (15L * 365L * 24L * 60L * 60L * 1000L)
        val result = useCase(
            firstName = "Carlos",
            lastName = "Perez",
            username = "carlos01",
            email = "carlos@example.com",
            phone = "69503355",
            address = "Av America 123 Cochabamba",
            birthDate = under18,
            password = "Password1",
            photoPath = null
        )
        assertTrue(result.isFailure)
        assertEquals("Debes ser mayor de 18 años", result.exceptionOrNull()?.message)
    }
}
