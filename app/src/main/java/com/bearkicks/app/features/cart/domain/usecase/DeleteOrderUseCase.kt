package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository

class DeleteOrderUseCase(private val repo: ICartRepository) {
    suspend operator fun invoke(orderId: String) = repo.deleteOrder(orderId)
}