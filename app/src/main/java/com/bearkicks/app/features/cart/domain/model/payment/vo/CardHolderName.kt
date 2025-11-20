package com.bearkicks.app.features.cart.domain.model.payment.vo

// Removed payment validation (simulated purchase). Retained empty stub.
@JvmInline
value class CardHolderName private constructor(val value: String) {
    companion object { fun create(input: String): Result<CardHolderName> = Result.success(CardHolderName("SIMULATED")) }
}
