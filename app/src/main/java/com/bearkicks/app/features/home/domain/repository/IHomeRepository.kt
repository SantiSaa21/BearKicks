package com.bearkicks.app.features.home.domain.repository

import com.bearkicks.app.features.home.domain.model.ShoeModel
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun watchShoes(): Flow<List<ShoeModel>>
    suspend fun toggleLike(id: String, liked: Boolean)
}