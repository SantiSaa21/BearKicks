package com.bearkicks.app.features.cart.domain.model

data class CartItem(
    val id: String,
    val shoeId: String,
    val name: String,
    val brand: String?,
    val price: Double,
    val imageUrl: String?,
    val size: Int?,
    val qty: Int
) {
    val subtotal: Double get() = price * qty
}