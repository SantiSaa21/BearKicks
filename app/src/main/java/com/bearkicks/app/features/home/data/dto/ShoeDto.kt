package com.bearkicks.app.features.home.data.dto

data class ShoeDto(
    val id: String = "",
    val name: String = "",
    val brand: String? = null,
    val price: Double = 0.0,
    val discountPrice: Double? = null,
    val imageUrl: String? = null,
    val isFeatured: Boolean? = false,
    val isNew: Boolean? = false
)
