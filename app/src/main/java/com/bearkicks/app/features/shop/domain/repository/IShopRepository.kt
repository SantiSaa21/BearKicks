package com.bearkicks.app.features.shop.domain.repository

import com.bearkicks.app.features.home.domain.model.ShoeModel
import kotlinx.coroutines.flow.Flow

interface IShopRepository {
    fun watchAllShoes(): Flow<List<ShoeModel>>
    fun watchById(id: String): Flow<ShoeModel?>
    suspend fun toggleLike(id: String, liked: Boolean)
}