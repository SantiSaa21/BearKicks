package com.bearkicks.application.features.home.domain.repository

import com.bearkicks.application.features.home.domain.model.ShoeModel
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    fun watchShoes(): Flow<List<ShoeModel>>
    suspend fun toggleLike(id: String, liked: Boolean)
}