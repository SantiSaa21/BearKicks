package com.bearkicks.app.features.cart.domain.model.payment.vo

// Removed payment validation – stub.
@JvmInline
value class Cvv private constructor(val value: String) { companion object { fun create(input: String): Result<Cvv> = Result.success(Cvv("000")) } }
