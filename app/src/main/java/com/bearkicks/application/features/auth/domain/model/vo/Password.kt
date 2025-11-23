package com.bearkicks.application.features.auth.domain.model.vo

import com.bearkicks.application.core.errors.DomainException
import com.bearkicks.application.core.errors.ErrorKey

@JvmInline
value class Password private constructor(val value: String) {
    companion object {
        private val LETTER = Regex("[A-Za-z]")
        private val DIGIT = Regex("[0-9]")
        fun create(input: String): Result<Password> {
            val v = input.trim()
            return if (v.length >= 8 && v.contains(LETTER) && v.contains(DIGIT))
                Result.success(Password(v))
            else Result.failure(DomainException(ErrorKey.INVALID_PASSWORD_RULES))
        }
    }
}
