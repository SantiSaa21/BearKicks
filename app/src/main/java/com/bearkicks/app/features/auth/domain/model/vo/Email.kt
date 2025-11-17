package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX = Regex(
            pattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            option = RegexOption.IGNORE_CASE
        )
        fun create(input: String): Result<Email> {
            val trimmed = input.trim()
            return if (EMAIL_REGEX.matches(trimmed)) Result.success(Email(trimmed))
            else Result.failure(IllegalArgumentException("Email inválido"))
        }
    }
}
