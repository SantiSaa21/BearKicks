package com.bearkicks.app.features.wishlist.data.datasource

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.data.database.dao.FavoriteDao
import com.bearkicks.app.features.wishlist.data.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WishListLocalDataSource(
    private val dao: FavoriteDao
) {
    fun observe(userId: String): Flow<List<ShoeModel>> =
        dao.observeAll(userId).map { list ->
            list.map {
                ShoeModel(
                    id = it.id,
                    name = it.name,
                    brand = it.brand,
                    price = it.price,
                    discountPrice = it.discountPrice,
                    imageUrl = it.imageUrl,
                    isFeatured = false,
                    isNew = false,
                    isLiked = true
                )
            }
        }

    fun observeIds(userId: String): Flow<Set<String>> = dao.observeAll(userId).map { list -> list.map { it.id }.toSet() }
    fun observeIsFavorite(userId: String, id: String): Flow<Boolean> = dao.observeIsFavorite(userId, id)

    suspend fun upsert(userId: String, shoe: ShoeModel) =
        dao.upsert(
            FavoriteEntity(
                id = shoe.id,
                userId = userId,
                name = shoe.name,
                brand = shoe.brand,
                price = shoe.price,
                discountPrice = shoe.discountPrice,
                imageUrl = shoe.imageUrl
            )
        )

    suspend fun delete(userId: String, id: String) = dao.delete(userId, id)
}
