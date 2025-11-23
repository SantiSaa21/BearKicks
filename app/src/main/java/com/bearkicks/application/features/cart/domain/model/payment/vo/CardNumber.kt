package com.bearkicks.application.features.cart.domain.model.payment.vo

// Removed payment validation – stub implementation.
@JvmInline
value class CardNumber private constructor(val sanitized: String) {
    val last4: String get() = "0000"
    val brand: String get() = "SIMULATED"
    companion object { fun create(input: String): Result<CardNumber> = Result.success(CardNumber("SIMULATED")) }
}
