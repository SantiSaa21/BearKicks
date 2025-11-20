package com.bearkicks.app.features.cart.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Payment observation removed. Retained for binary compatibility; always returns null.
class ObserveOrderPaymentUseCase {
    operator fun invoke(orderId: String): Flow<Nothing?> = flowOf(null)
}
