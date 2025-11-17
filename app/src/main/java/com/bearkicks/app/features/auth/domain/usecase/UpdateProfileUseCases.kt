package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import com.bearkicks.app.features.auth.domain.util.normalizeFirstName
import com.bearkicks.app.features.auth.domain.util.normalizeLastName
import com.bearkicks.app.features.auth.domain.util.normalizeAddress
import com.bearkicks.app.features.auth.domain.model.vo.Name
import com.bearkicks.app.features.auth.domain.model.vo.LastName
import com.bearkicks.app.features.auth.domain.model.vo.Phone
import com.bearkicks.app.features.auth.domain.model.vo.Address

class UpdateProfileUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        address: String? = null,
        photoPath: String? = null
    ): Result<UserModel> {
        // Validar solo campos presentes
        val validations = listOfNotNull(
            firstName?.let { Name.create(it) },
            lastName?.let { LastName.create(it) },
            phone?.let { Phone.create(it) },
            address?.let { Address.create(it) }
        )
        validations.forEach { if (it.isFailure) return Result.failure(it.exceptionOrNull()!!) }
        return repo.updateProfile(
            firstName?.let { normalizeFirstName(it) },
            lastName?.let { normalizeLastName(it) },
            phone?.trim(),
            address?.let { normalizeAddress(it) },
            photoPath
        )
    }
}

class UpdateProfilePhotoUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(photoPath: String): Result<UserModel> = repo.updateProfile(photoPath = photoPath)
}
