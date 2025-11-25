package com.bearkicks.application.features.cart.domain.model.payment.vo

@JvmInline
value class CardHolderName private constructor(val value: String) {
    companion object { fun create(input: String): Result<CardHolderName> = Result.success(CardHolderName("SIMULATED")) }
}
