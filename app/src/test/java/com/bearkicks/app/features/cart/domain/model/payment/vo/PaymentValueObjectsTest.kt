package com.bearkicks.app.features.cart.domain.model.payment.vo

import org.junit.Assert.*
import org.junit.Test

class PaymentValueObjectsTest {

    @Test
    fun `valid card number passes`() {
        val result = CardNumber.create("4111 1111 1111 1111")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invalid card number fails`() {
        val result = CardNumber.create("1234 0000")
        assertTrue(result.isFailure)
    }

    @Test
    fun `valid expiry date passes`() {
        val yr = (java.time.Year.now().value + 1) % 100
        val value = "12/%02d".format(yr)
        val result = ExpiryDate.create(value)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `past expiry date fails`() {
        val yr = (java.time.Year.now().value - 1) % 100
        val value = "01/%02d".format(yr)
        val result = ExpiryDate.create(value)
        assertTrue(result.isFailure)
    }

    @Test
    fun `valid cvv passes`() {
        assertTrue(Cvv.create("123").isSuccess)
    }

    @Test
    fun `invalid cvv fails`() {
        assertTrue(Cvv.create("1").isFailure)
    }

    @Test
    fun `valid card holder name passes`() {
        assertTrue(CardHolderName.create("Juan Perez").isSuccess)
    }

    @Test
    fun `invalid card holder name fails`() {
        assertTrue(CardHolderName.create("J").isFailure)
    }
}
