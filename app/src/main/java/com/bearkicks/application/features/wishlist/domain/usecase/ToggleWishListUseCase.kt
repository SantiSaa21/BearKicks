package com.bearkicks.application.features.wishlist.domain.usecase

import com.bearkicks.application.features.home.domain.model.ShoeModel
import com.bearkicks.application.features.wishlist.domain.repository.IWishListRepository

class ToggleWishListUseCase(private val repo: IWishListRepository) {
    suspend operator fun invoke(item: ShoeModel) {
        repo.setFavorite(item, !item.isLiked)
    }
}
