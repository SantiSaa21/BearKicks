package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Test

private class FakeUpdateRepo : IAuthRepository {
    private val state = MutableStateFlow<UserModel?>(null)
    var user = UserModel("uid","Carlos","Perez","carlos01","carlos@example.com","69503355","Av America 123 Cochabamba",null,null,System.currentTimeMillis(),System.currentTimeMillis())
    init { state.value = user }
    override suspend fun login(email: String, password: String) = Result.success(user)
    override suspend fun register(firstName: String, lastName: String, username: String, email: String, phone: String?, address: String?, birthDate: Long, password: String, photoPath: String?) = Result.success(user)
    override suspend fun logout(): Result<Unit> = Result.success(Unit)
    override fun observeUser(): Flow<UserModel?> = state
    override suspend fun getCurrentUser(): UserModel? = user
    override suspend fun updateProfile(firstName: String?, lastName: String?, phone: String?, address: String?, photoPath: String?): Result<UserModel> {
        firstName?.let { user = user.copy(firstName = it) }
        lastName?.let { user = user.copy(lastName = it) }
        phone?.let { user = user.copy(phone = it) }
        address?.let { user = user.copy(address = it) }
        photoPath?.let { user = user.copy(photoPath = it) }
        state.value = user
        return Result.success(user)
    }
}

class UpdateProfileUseCaseTest {
    private val repo = FakeUpdateRepo()
    private val useCase = UpdateProfileUseCase(repo)

    @Test fun update_valid() = runBlocking {
        val result = useCase(firstName = "Martin", lastName = "Lopez Perez", phone = "69503355", address = "Av America 999 Cochabamba")
        assertTrue(result.isSuccess)
        assertEquals("Martin", repo.user.firstName)
        assertEquals("Lopez Perez", repo.user.lastName)
    }

    @Test fun update_invalid_phone() = runBlocking {
        val result = useCase(phone = "123")
        assertTrue(result.isFailure)
        assertEquals("Teléfono inválido (8 dígitos iniciando en 6 o 7)", result.exceptionOrNull()?.message)
    }

    @Test fun update_invalid_address() = runBlocking {
        val result = useCase(address = "Av America 123 La Paz")
        assertTrue(result.isFailure)
        assertEquals("Dirección inválida (10-120 caracteres y debe incluir Cochabamba)", result.exceptionOrNull()?.message)
    }
}
