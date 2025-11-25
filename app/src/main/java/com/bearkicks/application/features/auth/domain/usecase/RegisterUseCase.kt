package com.bearkicks.application.features.auth.domain.usecase

import com.bearkicks.application.features.auth.domain.model.vo.Address
import com.bearkicks.application.features.auth.domain.model.vo.Email
import com.bearkicks.application.features.auth.domain.model.vo.LastName
import com.bearkicks.application.features.auth.domain.model.vo.Name
import com.bearkicks.application.features.auth.domain.model.vo.Password
import com.bearkicks.application.features.auth.domain.model.vo.Phone
import com.bearkicks.application.features.auth.domain.model.vo.Username
import com.bearkicks.application.features.auth.domain.model.vo.BirthDate
import com.bearkicks.application.features.auth.domain.util.normalizeFirstName
import com.bearkicks.application.features.auth.domain.util.normalizeLastName
import com.bearkicks.application.features.auth.domain.util.normalizeAddress
import com.bearkicks.application.features.auth.domain.repository.IAuthRepository

class RegisterUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String?,
        address: String?,
        birthDate: Long,
        password: String,
        photoPath: String? = null
    ) =
        Name.create(firstName)
            .flatMap { LastName.create(lastName) }
            .flatMap { Username.create(username) }
            .flatMap { Email.create(email) }
            .flatMap { Phone.create(phone ?: "") }
            .flatMap { Address.create(address ?: "") }
            .flatMap { BirthDate.create(birthDate) }
            .flatMap { Password.create(password).map {} }
            .fold(
                onSuccess = {
                    repo.register(
                        normalizeFirstName(firstName),
                        normalizeLastName(lastName),
                        username.trim(),
                        email.trim(),
                        phone?.trim(),
                        address?.let { normalizeAddress(it) },
                        birthDate,
                        password.trim(),
                        photoPath
                    )
                },
                onFailure = { Result.failure(it) }
            )
}

private inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(onSuccess = transform, onFailure = { Result.failure(it) })
