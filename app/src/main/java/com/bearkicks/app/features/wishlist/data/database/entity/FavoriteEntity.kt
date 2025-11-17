package com.bearkicks.app.features.wishlist.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val brand: String?,
    val price: Double,
    val discountPrice: Double?,
    val imageUrl: String?
)
