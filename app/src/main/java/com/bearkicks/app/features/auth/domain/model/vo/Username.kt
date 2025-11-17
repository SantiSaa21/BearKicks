package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Username private constructor(val value: String) {
    companion object {
        private val REGEX = Regex("^[A-Za-z0-9_-]{4,25}$")
        fun create(input: String): Result<Username> {
            val v = input.trim()
            return if (REGEX.matches(v)) Result.success(Username(v))
            else Result.failure(IllegalArgumentException("Username inválido (4-25 caracteres: letras, números, guion y guion bajo)"))
        }
    }
}
