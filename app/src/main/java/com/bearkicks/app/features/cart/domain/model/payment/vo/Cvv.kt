package com.bearkicks.app.features.cart.domain.model.payment.vo

@JvmInline
value class Cvv private constructor(val value: String) {
    companion object {
        private val REGEX = Regex("^[0-9]{3,4}$")
        fun create(input: String): Result<Cvv> {
            val v = input.trim()
            return if (REGEX.matches(v)) Result.success(Cvv(v)) else Result.failure(IllegalArgumentException("CVV inválido"))
        }
    }
}
