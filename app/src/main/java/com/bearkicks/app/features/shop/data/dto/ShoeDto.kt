package com.bearkicks.app.features.shop.data.dto

import androidx.annotation.Keep
import com.google.firebase.database.Exclude

@Keep
data class ShoeDto(
    var id: String = "",
    var name: String = "",
    var brand: String? = null,
    var price: Double = 0.0,
    var discountPrice: Double? = null,
    var isFeatured: Boolean = false,
    var isNew: Boolean = false,
    var imageUrl: String? = null,
    var isLiked: Boolean? = null,
    var isliked: Boolean? = null,
    var description: String? = null,
    var sizes: List<Int>? = null
) {
    @get:Exclude val liked: Boolean get() = isLiked ?: isliked ?: false
}