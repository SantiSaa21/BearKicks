package com.bearkicks.app.features.wishlist.data.repository

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.data.datasource.FavoritesRemoteDataSource
import com.bearkicks.app.features.wishlist.data.datasource.WishListLocalDataSource
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.auth.FirebaseAuth

class WishListRepository(
    private val local: WishListLocalDataSource,
    private val remote: FavoritesRemoteDataSource
) : IWishListRepository {
    private fun uid(): String = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    override fun observeFavorites(): Flow<List<ShoeModel>> = local.observe(uid())
    override fun observeFavoriteIds(): Flow<Set<String>> = local.observeIds(uid())
    override fun observeIsFavorite(id: String): Flow<Boolean> = local.observeIsFavorite(uid(), id)
    override suspend fun setFavorite(shoe: ShoeModel, favored: Boolean) {
        val userId = uid()
        if (favored) local.upsert(userId, shoe) else local.delete(userId, shoe.id)
        runCatching { remote.setFavorite(shoe, favored) }
    }
}
