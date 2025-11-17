package com.bearkicks.app.features.cart.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val shoeId: String,
    val name: String,
    val brand: String?,
    val price: Double,
    val qty: Int,
    val imageUrl: String?,
    val size: Int?
)