package com.bearkicks.app.features.wishlist.domain.usecase

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import kotlinx.coroutines.flow.Flow

class ObserveWishListUseCase(private val repo: IWishListRepository) {
    operator fun invoke(): Flow<List<ShoeModel>> = repo.observeFavorites()
}