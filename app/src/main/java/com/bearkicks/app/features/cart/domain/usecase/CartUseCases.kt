package com.bearkicks.app.features.cart.domain.usecase

import com.bearkicks.app.features.cart.data.ICartRepository
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.data.database.entity.OrderEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow
import com.bearkicks.app.features.cart.domain.model.payment.PaymentInfo
import com.bearkicks.app.features.cart.domain.model.payment.vo.CardNumber
import com.bearkicks.app.features.cart.domain.model.payment.vo.Cvv
import com.bearkicks.app.features.cart.domain.model.payment.vo.ExpiryDate
import com.bearkicks.app.features.cart.domain.model.payment.vo.CardHolderName

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

class CheckoutWithCardUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    suspend operator fun invoke(items: List<CartItem>, number: String, expiry: String, cvv: String, holder: String): Result<String> {
        if (items.isEmpty()) return Result.failure(com.bearkicks.app.core.errors.DomainException(com.bearkicks.app.core.errors.ErrorKey.CART_EMPTY))
        val cardNumber = CardNumber.create(number).getOrElse { return Result.failure(it) }
        val exp = ExpiryDate.create(expiry).getOrElse { return Result.failure(it) }
        val c = Cvv.create(cvv).getOrElse { return Result.failure(it) }
        val h = CardHolderName.create(holder).getOrElse { return Result.failure(it) }
        val total = items.sumOf { it.subtotal }
        val payment = PaymentInfo.Card(cardNumber.brand, cardNumber.last4)
        val id = repo.placeOrderWithPayment(uidProvider(), items, total, payment)
        return Result.success(id)
    }
}

class CheckoutWithQrUseCase(private val repo: ICartRepository, private val uidProvider: () -> String) {
    /**
     * Checkout con QR usando timestamp pre-generado para que el payload mostrado en la UI
     * coincida exactamente con el hash almacenado.
     * payload = "$provider:$total:$timestamp"
     * Devuelve Pair(orderId, payloadHash).
     */
        suspend operator fun invoke(items: List<CartItem>, provider: String, timestamp: Long): Result<Pair<String, String>> {
            if (items.isEmpty()) return Result.failure(com.bearkicks.app.core.errors.DomainException(com.bearkicks.app.core.errors.ErrorKey.CART_EMPTY))
            val total = items.sumOf { it.subtotal }
            val payload = "${'$'}provider:${'$'}total:${'$'}timestamp"
            val hash = sha256(payload)
            val payment = PaymentInfo.Qr(provider, hash)
            val id = repo.placeOrderWithPayment(uidProvider(), items, total, payment)
            return Result.success(id to hash)
        }
        private fun sha256(input: String): String {
            val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { b -> "%02x".format(b) }
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
