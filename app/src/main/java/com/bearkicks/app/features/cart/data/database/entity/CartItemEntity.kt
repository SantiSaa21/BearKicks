package com.bearkicks.app.features.cart.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val shoeId: String,
    val name: String,
    val brand: String?,
    val price: Double,
    val imageUrl: String?,
    val size: Int?,
    val qty: Int
)