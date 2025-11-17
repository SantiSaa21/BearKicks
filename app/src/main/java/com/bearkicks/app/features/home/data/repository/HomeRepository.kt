package com.bearkicks.app.features.home.data.repository

import com.bearkicks.app.features.home.data.datasource.HomeRemoteDataSource
import com.bearkicks.app.features.home.data.mapper.toDomain
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepository(private val remote: HomeRemoteDataSource) : IHomeRepository {
    override fun watchShoes(): Flow<List<ShoeModel>> =
        remote.watchShoesDto().map { list -> list.map { it.toDomain() } }
    override suspend fun toggleLike(id: String, liked: Boolean) {
        remote.toggleLike(id, liked)
    }
}