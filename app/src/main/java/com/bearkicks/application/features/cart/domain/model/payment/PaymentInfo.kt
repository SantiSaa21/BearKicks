package com.bearkicks.application.features.cart.domain.model.payment

sealed interface PaymentInfo { val method: Method; enum class Method { SIMULATED } }
