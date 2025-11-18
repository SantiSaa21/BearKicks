package com.bearkicks.app.features.auth.domain.model.vo

import kotlin.math.abs
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

@JvmInline
value class BirthDate private constructor(val epochMillis: Long) {
    companion object {
        // 18 años aproximados (365 * 18 + 4 días de bisiesto) = 6574 días
        private const val MILLIS_18_YEARS: Long = 18L * 365L * 24L * 60L * 60L * 1000L + 4L * 24L * 60L * 60L * 1000L
        fun create(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): Result<BirthDate> {
            val ageOk = (nowMillis - epochMillis) >= MILLIS_18_YEARS
            return if (epochMillis > 0 && ageOk) Result.success(BirthDate(epochMillis))
            else Result.failure(DomainException(ErrorKey.MUST_BE_ADULT))
        }
    }
}
