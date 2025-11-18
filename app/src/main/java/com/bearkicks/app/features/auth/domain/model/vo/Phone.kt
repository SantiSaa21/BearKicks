package com.bearkicks.app.features.auth.domain.model.vo

import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

@JvmInline
value class Phone private constructor(val value: String) {
    companion object {
        // Cochabamba: 8 dígitos iniciando en 6 o 7
        private val PHONE_REGEX = Regex("^[67][0-9]{7}$")
        fun create(input: String): Result<Phone> {
            val v = input.trim()
            return if (PHONE_REGEX.matches(v)) Result.success(Phone(v))
            else Result.failure(DomainException(ErrorKey.INVALID_PHONE_RULES))
        }
    }
}

@JvmInline
value class Address private constructor(val value: String) {
    companion object {
        // 10-120 caracteres, debe contener 'cochabamba'
        private val ADDRESS_REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .,#-]{10,120}$")
        fun create(input: String): Result<Address> {
            val v = input.trim()
            val ok = ADDRESS_REGEX.matches(v) && v.lowercase().contains("cochabamba")
            return if (ok) Result.success(Address(v))
            else Result.failure(DomainException(ErrorKey.INVALID_ADDRESS_RULES))
        }
    }
}
