package com.bearkicks.app.features.shop.domain.usecase

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.domain.repository.IShopRepository
import kotlinx.coroutines.flow.Flow

class GetShoeDetailUseCase(private val repo: IShopRepository) {
    operator fun invoke(id: String): Flow<ShoeModel?> = repo.watchById(id)
}