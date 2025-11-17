package com.bearkicks.app.features.cart.domain.model.payment.vo

@JvmInline
value class CardNumber private constructor(val sanitized: String) {
    val last4: String get() = sanitized.takeLast(4)
    val brand: String get() = when {
        sanitized.startsWith("4") -> "VISA"
        sanitized.startsWith("5") -> "MASTERCARD"
        sanitized.startsWith("3") -> "AMEX"
        else -> "GENERIC"
    }
    companion object {
        fun create(input: String): Result<CardNumber> {
            val digits = input.filter { it.isDigit() }
            if (digits.length !in 13..19) return Result.failure(IllegalArgumentException("Número inválido"))
            if (!luhn(digits)) return Result.failure(IllegalArgumentException("Número de tarjeta inválido"))
            return Result.success(CardNumber(digits))
        }
        private fun luhn(number: String): Boolean {
            var sum = 0
            var alt = false
            for (i in number.length - 1 downTo 0) {
                var n = number[i].code - 48
                if (alt) {
                    n *= 2
                    if (n > 9) n -= 9
                }
                sum += n
                alt = !alt
            }
            return sum % 10 == 0
        }
    }
}
