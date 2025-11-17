package com.bearkicks.app.features.home.domain.model

data class ShoeModel(
    val id: String,
    val name: String,
    val brand: String?,
    val price: Double,
    val discountPrice: Double?,
    val imageUrl: String?,
    val isFeatured: Boolean,
    val isNew: Boolean,
    val isLiked: Boolean = false,
    val sizes: List<Int>? = null
)
