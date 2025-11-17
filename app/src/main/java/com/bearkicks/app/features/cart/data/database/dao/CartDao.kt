package com.bearkicks.app.features.cart.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bearkicks.app.features.cart.data.database.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart WHERE userId = :userId")
    fun observeCart(userId: String): Flow<List<CartItemEntity>>

    @Query("SELECT COUNT(*) > 0 FROM cart WHERE userId = :userId AND shoeId = :shoeId")
    fun observeIsInCart(userId: String, shoeId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) > 0 FROM cart WHERE userId = :userId AND shoeId = :shoeId AND size = :size")
    fun observeIsInCart(userId: String, shoeId: String, size: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Update
    suspend fun update(item: CartItemEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clear(userId: String)
}
