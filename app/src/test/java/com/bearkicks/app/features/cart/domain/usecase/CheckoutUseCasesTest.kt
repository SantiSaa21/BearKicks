package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository
import com.bearkicks.app.features.cart.domain.model.CartItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

private class FakeCartRepo : ICartRepository {
    override fun observeCart(uid: String) = kotlinx.coroutines.flow.flow<List<CartItem>> { emit(emptyList()) }
    override fun observeIsInCart(userId: String, shoeId: String) = kotlinx.coroutines.flow.flow { emit(false) }
    override fun observeIsInCart(userId: String, shoeId: String, size: Int) = kotlinx.coroutines.flow.flow { emit(false) }
    override suspend fun upsert(uid: String, item: CartItem) {}
    override suspend fun remove(id: String) {}
    override suspend fun clear(uid: String) {}
    override suspend fun placeOrder(uid: String, items: List<CartItem>, total: Double): String = "order-1"
    override fun observeOrders(uid: String) = kotlinx.coroutines.flow.flow<List<com.bearkicks.app.features.cart.data.database.entity.OrderEntity>> { emit(emptyList()) }
    override fun observeOrderItems(orderId: String) = kotlinx.coroutines.flow.flow<List<com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity>> { emit(emptyList()) }
    override suspend fun clearOrders(uid: String) {}
    override suspend fun deleteOrder(orderId: String) {}
}

class CheckoutUseCasesTest {
    private val repo = FakeCartRepo()
    private val uid = { "test-user" }

    @Test
    fun `place order returns id`() = runBlocking {
        val useCase = PlaceOrderUseCase(repo, uid)
        val items = listOf(CartItem(id = "", shoeId = "shoe1", name = "Shoe", brand = "Brand", price = 10.0, imageUrl = null, size = 42, qty = 1))
        val id = useCase(items)
        assertEquals("order-1", id)
    }
}
