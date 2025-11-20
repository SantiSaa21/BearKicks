package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.data.database.entity.OrderEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class ObserveCartUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    operator fun invoke(): Flow<List<CartItem>> = repo.observeCart(uidProvider())
}
class AddToCartUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    suspend operator fun invoke(item: CartItem) = repo.upsert(uidProvider(), item)
}
class RemoveFromCartUseCase(private val repo: ICartRepository) {
    suspend operator fun invoke(id: String) = repo.remove(id)
}
class ClearCartUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    suspend operator fun invoke() = repo.clear(uidProvider())
}
open class PlaceOrderUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    suspend operator fun invoke(items: List<CartItem>): String {
        val total = items.sumOf { it.subtotal }
        return repo.placeOrder(uidProvider(), items, total)
    }
}


class ObserveOrdersUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    operator fun invoke() = repo.observeOrders(uidProvider())
}
class ObserveOrderItemsUseCase(private val repo: ICartRepository) {
    operator fun invoke(orderId: String) = repo.observeOrderItems(orderId)
}

class ObserveIsInCartUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    operator fun invoke(shoeId: String): Flow<Boolean> = repo.observeIsInCart(uidProvider(), shoeId)
    operator fun invoke(shoeId: String, size: Int): Flow<Boolean> = repo.observeIsInCart(uidProvider(), shoeId, size)
}

class ClearOrdersUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    suspend operator fun invoke() = repo.clearOrders(uidProvider())
}
