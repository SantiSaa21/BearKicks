package com.bearkicks.app.features.wishlist.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.bearkicks.app.features.wishlist.data.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY name")
    fun observeAll(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id AND userId = :userId)")
    fun observeIsFavorite(userId: String, id: String): Flow<Boolean>

    @Upsert
    suspend fun upsert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id AND userId = :userId")
    suspend fun delete(userId: String, id: String)
}
