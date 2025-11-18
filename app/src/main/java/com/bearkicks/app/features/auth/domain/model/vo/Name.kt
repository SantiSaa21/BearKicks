package com.bearkicks.app.features.auth.domain.model.vo

import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

@JvmInline
value class Name private constructor(val value: String) {
    companion object {
        // Solo letras (sin espacios) 2-50
        private val REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,50}$")
        fun create(input: String): Result<Name> {
            val v = input.trim()
            return if (REGEX.matches(v)) Result.success(Name(v))
            else Result.failure(DomainException(ErrorKey.INVALID_FIRST_NAME_RULES))
        }
    }
}

@JvmInline
value class LastName private constructor(val value: String) {
    companion object {
        // Apellidos compuestos separados por espacios, cada parte >=2 letras, total <=60
        private val REGEX = Regex("^([A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,})( [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,})*$")
        fun create(input: String): Result<LastName> {
            val v = input.trim()
            return if (v.length <= 60 && REGEX.matches(v)) Result.success(LastName(v))
            else Result.failure(DomainException(ErrorKey.INVALID_LAST_NAME_RULES))
        }
    }
}
