package com.bearkicks.app.features.shop.data.repository

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.data.datasource.ShopRemoteDataSource
import com.bearkicks.app.features.shop.data.mapper.toDomain
import com.bearkicks.app.features.shop.domain.repository.IShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopRepository(private val remote: ShopRemoteDataSource) : IShopRepository {
    override fun watchAllShoes(): Flow<List<ShoeModel>> =
        remote.watchShoesDto().map { it.map { dto -> dto.toDomain() } }
    override fun watchById(id: String): Flow<ShoeModel?> =
        remote.watchByIdDto(id).map { it?.toDomain() }
    override suspend fun toggleLike(id: String, liked: Boolean) {
        remote.toggleLike(id, liked)
    }
}