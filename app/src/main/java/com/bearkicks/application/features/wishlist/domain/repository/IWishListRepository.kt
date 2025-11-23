package com.bearkicks.application.features.wishlist.domain.repository

import com.bearkicks.application.features.home.domain.model.ShoeModel
import kotlinx.coroutines.flow.Flow

interface IWishListRepository {
    fun observeFavorites(): Flow<List<ShoeModel>>
    fun observeFavoriteIds(): Flow<Set<String>>
    fun observeIsFavorite(id: String): Flow<Boolean>
    suspend fun setFavorite(shoe: ShoeModel, favored: Boolean)
}
