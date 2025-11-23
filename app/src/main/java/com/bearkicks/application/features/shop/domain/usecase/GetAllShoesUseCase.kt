package com.bearkicks.application.features.shop.domain.usecase

import com.bearkicks.application.features.home.domain.model.ShoeModel
import com.bearkicks.application.features.shop.domain.repository.IShopRepository
import kotlinx.coroutines.flow.Flow

class GetAllShoesUseCase(private val repo: IShopRepository) {
    operator fun invoke(): Flow<List<ShoeModel>> = repo.watchAllShoes()
}