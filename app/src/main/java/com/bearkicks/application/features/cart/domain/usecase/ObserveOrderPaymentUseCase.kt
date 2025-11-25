package com.bearkicks.application.features.cart.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveOrderPaymentUseCase {
    operator fun invoke(orderId: String): Flow<Nothing?> = flowOf(null)
}
