package com.bearkicks.application.features.wishlist.domain.usecase

import com.bearkicks.application.features.home.domain.model.ShoeModel
import com.bearkicks.application.features.wishlist.domain.repository.IWishListRepository
import kotlinx.coroutines.flow.Flow

class ObserveWishListUseCase(private val repo: IWishListRepository) {
    operator fun invoke(): Flow<List<ShoeModel>> = repo.observeFavorites()
}