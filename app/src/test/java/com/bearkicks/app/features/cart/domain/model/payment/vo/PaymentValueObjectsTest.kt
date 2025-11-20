package com.bearkicks.app.features.cart.domain.model.payment.vo

import org.junit.Assert.*
import org.junit.Test

// Payment value object tests replaced after removal of real payment logic.
class PaymentValueObjectsTest {
    @Test
    fun `stub card number returns simulated brand`() {
        val card = CardNumber.create("any").getOrThrow()
        assertEquals("SIMULATED", card.brand)
        assertEquals("0000", card.last4)
    }
}
