package com.bearkicks.app.features.cart.domain.model.payment.vo

import java.time.YearMonth

@JvmInline
value class ExpiryDate private constructor(val value: String) {
    companion object {
        private val REGEX = Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")
        fun create(input: String): Result<ExpiryDate> {
            val v = input.trim()
            if (!REGEX.matches(v)) return Result.failure(IllegalArgumentException("Formato MM/YY inválido"))
            val parts = v.split("/")
            val month = parts[0].toInt()
            val year = parts[1].toInt() + 2000
            val ym = YearMonth.of(year, month)
            val now = YearMonth.now()
            return if (ym < now) Result.failure(IllegalArgumentException("La tarjeta está vencida"))
            else Result.success(ExpiryDate(v))
        }
    }
}
