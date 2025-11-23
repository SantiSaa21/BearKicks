package com.bearkicks.application.features.shop.data.mapper

import com.bearkicks.application.features.home.domain.model.ShoeModel
import com.bearkicks.application.features.shop.data.dto.ShoeDto

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
