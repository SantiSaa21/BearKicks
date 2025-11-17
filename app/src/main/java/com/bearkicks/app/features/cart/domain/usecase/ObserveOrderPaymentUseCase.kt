package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository

class ObserveOrderPaymentUseCase(private val repo: ICartRepository) {
    operator fun invoke(orderId: String) = repo.observePayment(orderId)
}
