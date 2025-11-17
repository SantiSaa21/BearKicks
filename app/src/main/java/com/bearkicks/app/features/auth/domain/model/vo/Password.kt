package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Password private constructor(val value: String) {
    companion object {
        private val LETTER = Regex("[A-Za-z]")
        private val DIGIT = Regex("[0-9]")
        fun create(input: String): Result<Password> {
            val v = input.trim()
            return if (v.length >= 8 && v.contains(LETTER) && v.contains(DIGIT))
                Result.success(Password(v))
            else Result.failure(IllegalArgumentException("La contraseña debe tener mínimo 8 caracteres, con letras y números"))
        }
    }
}
