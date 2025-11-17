package com.bearkicks.app.features.home.data.mapper

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.data.dto.ShoeDto

fun ShoeDto.toDomain(): ShoeModel = ShoeModel(
    id = id,
    name = name,
    brand = brand,
    price = price,
    discountPrice = discountPrice,
    imageUrl = imageUrl,
    isFeatured = isFeatured,
    isNew = isNew,
    isLiked = liked,
    sizes = sizes
)
