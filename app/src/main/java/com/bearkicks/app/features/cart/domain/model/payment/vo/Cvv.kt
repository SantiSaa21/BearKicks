package com.bearkicks.app.features.cart.domain.model.payment.vo

import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

@JvmInline
value class Cvv private constructor(val value: String) {
    companion object {
        private val REGEX = Regex("^[0-9]{3,4}$")
        fun create(input: String): Result<Cvv> {
            val v = input.trim()
            return if (REGEX.matches(v)) Result.success(Cvv(v)) else Result.failure(DomainException(ErrorKey.CVV_INVALID))
        }
    }
}
