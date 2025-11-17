package com.bearkicks.app.features.cart.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_payment")
data class OrderPaymentEntity(
    @PrimaryKey val orderId: String,
    val method: String,
    val brand: String?,
    val last4: String?,
    val provider: String?,
    val payloadHash: String?
)
