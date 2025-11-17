package com.bearkicks.app.features.cart.domain.model.payment.vo

@JvmInline
value class CardHolderName private constructor(val value: String) {
    companion object {
        private val REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,40}$")
        fun create(input: String): Result<CardHolderName> {
            val v = input.trim().replace(Regex("\\s+"), " ")
            if (!REGEX.matches(v)) return Result.failure(IllegalArgumentException("Nombre inválido"))
            return Result.success(CardHolderName(v))
        }
    }
}
