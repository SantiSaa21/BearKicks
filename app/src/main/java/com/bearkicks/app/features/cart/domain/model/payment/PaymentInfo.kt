package com.bearkicks.app.features.cart.domain.model.payment

sealed interface PaymentInfo {
    val method: Method

    enum class Method { CARD, QR }

    data class Card(
        val brand: String,
        val last4: String
    ) : PaymentInfo { override val method = Method.CARD }

    data class Qr(
        val provider: String,
        val payloadHash: String
    ) : PaymentInfo { override val method = Method.QR }
}
