package com.bearkicks.application.features.cart.domain.model.payment.vo

// Removed payment validation – stub.
@JvmInline
value class ExpiryDate private constructor(val value: String) { companion object { fun create(input: String): Result<ExpiryDate> = Result.success(ExpiryDate("00/00")) } }
