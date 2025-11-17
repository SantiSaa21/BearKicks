package com.bearkicks.app.features.cart.domain.model

import com.bearkicks.app.features.cart.domain.model.payment.PaymentInfo

data class OrderModel(
    val id: String,
    val userId: String,
    val createdAt: Long,
    val total: Double,
    val payment: PaymentInfo?
)
