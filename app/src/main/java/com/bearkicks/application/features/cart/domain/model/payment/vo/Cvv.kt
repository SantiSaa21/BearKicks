package com.bearkicks.application.features.cart.domain.model.payment.vo

@JvmInline
value class Cvv private constructor(val value: String) { companion object { fun create(input: String): Result<Cvv> = Result.success(Cvv("000")) } }
