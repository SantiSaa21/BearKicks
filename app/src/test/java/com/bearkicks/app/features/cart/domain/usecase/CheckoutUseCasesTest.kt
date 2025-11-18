package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.domain.model.payment.PaymentInfo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

private class FakeCartRepo : ICartRepository {
    var lastPayment: PaymentInfo? = null
    override fun observeCart(uid: String) = kotlinx.coroutines.flow.flow<List<CartItem>> { emit(emptyList()) }
    override fun observeIsInCart(userId: String, shoeId: String) = kotlinx.coroutines.flow.flow { emit(false) }
    override fun observeIsInCart(userId: String, shoeId: String, size: Int) = kotlinx.coroutines.flow.flow { emit(false) }
    override suspend fun upsert(uid: String, item: CartItem) {}
    override suspend fun remove(id: String) {}
    override suspend fun clear(uid: String) {}
    override suspend fun placeOrder(uid: String, items: List<CartItem>, total: Double): String = "order-1"
    override suspend fun placeOrderWithPayment(uid: String, items: List<CartItem>, total: Double, payment: PaymentInfo): String {
        lastPayment = payment
        return "order-2"
    }
    override fun observeOrders(uid: String) = kotlinx.coroutines.flow.flow<List<com.bearkicks.app.features.cart.data.database.entity.OrderEntity>> { emit(emptyList()) }
    override fun observeOrderItems(orderId: String) = kotlinx.coroutines.flow.flow<List<com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity>> { emit(emptyList()) }
    override fun observePayment(orderId: String) = kotlinx.coroutines.flow.flow<com.bearkicks.app.features.cart.data.database.entity.OrderPaymentEntity?> { emit(null) }
    override suspend fun clearOrders(uid: String) {}
    override suspend fun deleteOrder(orderId: String) {}
}

class CheckoutUseCasesTest {
    private val repo = FakeCartRepo()
    private val uid = { "test-user" }

    @Test
    fun `card checkout empty cart returns CART_EMPTY`() = runBlocking {
        val useCase = CheckoutWithCardUseCase(repo, uid)
        val result = useCase(emptyList(), "4111111111111111", "12/30", "123", "Juan Perez")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CART_EMPTY, ex.key)
    }

    @Test
    fun `qr checkout empty cart returns CART_EMPTY`() = runBlocking {
        val useCase = CheckoutWithQrUseCase(repo, uid)
        val result = useCase(emptyList(), "QRPay", System.currentTimeMillis())
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CART_EMPTY, ex.key)
    }

    @Test
    fun `card checkout success returns order id and payment info`() = runBlocking {
        val repo = FakeCartRepo()
        val useCase = CheckoutWithCardUseCase(repo, uid)
        val items = listOf(
            CartItem(id = "", shoeId = "shoe1", name = "Shoe", brand = "Brand", price = 100.0, imageUrl = null, size = 42, qty = 2)
        )
        val result = useCase(items, "4111 1111 1111 1111", "12/30", "123", "Juan Perez")
        assertTrue(result.isSuccess)
        assertEquals("order-2", result.getOrNull())
        val p = repo.lastPayment as PaymentInfo.Card
        assertEquals("VISA", p.brand)
        assertEquals("1111", p.last4)
    }

    @Test
    fun `qr checkout success returns order id and hash`() = runBlocking {
        val repo = FakeCartRepo()
        val useCase = CheckoutWithQrUseCase(repo, uid)
        val items = listOf(
            CartItem(id = "", shoeId = "shoe1", name = "Shoe", brand = "Brand", price = 50.0, imageUrl = null, size = 41, qty = 1)
        )
        val ts = 1731900000000 // fixed timestamp
        val result = useCase(items, "QRPay", ts)
        assertTrue(result.isSuccess)
        val (orderId, hash) = result.getOrNull()!!
        assertEquals("order-2", orderId)
        assertEquals(64, hash.length)
        val p = repo.lastPayment as PaymentInfo.Qr
        assertEquals("QRPay", p.provider)
        assertEquals(64, p.payloadHash.length)
    }
}
