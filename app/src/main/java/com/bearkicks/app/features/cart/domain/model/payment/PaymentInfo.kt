package com.bearkicks.app.features.cart.domain.model.payment

// PaymentInfo removed (simulated purchases only). Placeholder kept to avoid lingering references.
sealed interface PaymentInfo { val method: Method; enum class Method { SIMULATED } }
