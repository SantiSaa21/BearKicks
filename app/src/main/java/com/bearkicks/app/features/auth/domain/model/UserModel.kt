package com.bearkicks.app.features.auth.domain.model

data class UserModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val birthDate: Long?,
    val photoPath: String?,
    val createdAt: Long?,
    val updatedAt: Long?
)
