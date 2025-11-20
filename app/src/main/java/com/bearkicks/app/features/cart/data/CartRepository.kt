package com.bearkicks.app.features.cart.data

import com.bearkicks.app.features.cart.data.database.dao.CartDao
import com.bearkicks.app.features.cart.data.database.dao.OrdersDao
import com.bearkicks.app.features.cart.data.database.entity.CartItemEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity
import com.bearkicks.app.features.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface ICartRepository {
    fun observeCart(userId: String): Flow<List<CartItem>>
    fun observeIsInCart(userId: String, shoeId: String): Flow<Boolean>
    fun observeIsInCart(userId: String, shoeId: String, size: Int): Flow<Boolean>
    suspend fun upsert(userId: String, item: CartItem)
    suspend fun remove(id: String)
    suspend fun clear(userId: String)
    suspend fun placeOrder(userId: String, items: List<CartItem>, total: Double): String
    fun observeOrders(userId: String): Flow<List<OrderEntity>>
    fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>>
    suspend fun clearOrders(userId: String)
    suspend fun deleteOrder(orderId: String)
}

class CartRepository(
    private val cartDao: CartDao,
    private val ordersDao: OrdersDao
) : ICartRepository {
    override fun observeCart(userId: String): Flow<List<CartItem>> =
        cartDao.observeCart(userId).map { list ->
            list.map { it.toDomain() }
        }

    override fun observeIsInCart(userId: String, shoeId: String): Flow<Boolean> =
        cartDao.observeIsInCart(userId, shoeId)

    override fun observeIsInCart(userId: String, shoeId: String, size: Int): Flow<Boolean> =
        cartDao.observeIsInCart(userId, shoeId, size)

    override suspend fun upsert(userId: String, item: CartItem) {
        val id = item.id.ifEmpty { buildId(userId, item.shoeId, item.size) }
        cartDao.upsert(item.toEntity(userId, id))
    }

    override suspend fun remove(id: String) { cartDao.delete(id) }

    override suspend fun clear(userId: String) { cartDao.clear(userId) }

    override suspend fun placeOrder(userId: String, items: List<CartItem>, total: Double): String {
        val orderId = UUID.randomUUID().toString()
        ordersDao.insertOrder(OrderEntity(orderId, userId, System.currentTimeMillis(), total))
        ordersDao.insertItems(items.map { it.toOrderItem(orderId) })
        cartDao.clear(userId)
        return orderId
    }


    override fun observeOrders(userId: String): Flow<List<OrderEntity>> = ordersDao.observeOrders(userId)
    override fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>> = ordersDao.observeOrderItems(orderId)
    override suspend fun clearOrders(userId: String) { ordersDao.clearUserOrders(userId) }
    override suspend fun deleteOrder(orderId: String) { ordersDao.deleteOrderCascade(orderId) }

    private fun buildId(userId: String, shoeId: String, size: Int?): String =
        listOfNotNull(userId, shoeId, size?.toString()).joinToString(":")

    private fun CartItemEntity.toDomain() = CartItem(id, shoeId, name, brand, price, imageUrl, size, qty)
    private fun CartItem.toEntity(userId: String, id: String) = CartItemEntity(id, userId, shoeId, name, brand, price, imageUrl, size, qty)
    private fun CartItem.toOrderItem(orderId: String) = OrderItemEntity(
        id = UUID.randomUUID().toString(),
        orderId = orderId,
        shoeId = shoeId,
        name = name,
        brand = brand,
        price = price,
        qty = qty,
        imageUrl = imageUrl,
        size = size
    )
}
