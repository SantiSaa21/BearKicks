package com.bearkicks.app.features.cart.domain.model.payment.vo

import org.junit.Assert.*
import org.junit.Test
import com.bearkicks.app.core.errors.DomainException
import com.bearkicks.app.core.errors.ErrorKey

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
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CARD_NUMBER_INVALID_LENGTH, ex.key)
    }

    @Test
    fun `invalid luhn card number returns correct key`() {
        val result = CardNumber.create("4111 1111 1111 1112")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CARD_NUMBER_LUHN_INVALID, ex.key)
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
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.EXPIRY_EXPIRED, ex.key)
    }

    @Test
    fun `invalid expiry format returns correct key`() {
        val result = ExpiryDate.create("13/25")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.EXPIRY_FORMAT_INVALID, ex.key)
    }

    @Test
    fun `valid cvv passes`() {
        assertTrue(Cvv.create("123").isSuccess)
    }

    @Test
    fun `invalid cvv fails`() {
        val result = Cvv.create("1")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CVV_INVALID, ex.key)
    }

    @Test
    fun `valid card holder name passes`() {
        assertTrue(CardHolderName.create("Juan Perez").isSuccess)
    }

    @Test
    fun `invalid card holder name fails`() {
        val result = CardHolderName.create("J")
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as DomainException
        assertEquals(ErrorKey.CARDHOLDER_INVALID, ex.key)
    }

    @Test
    fun `brand detection for common cards`() {
        val visa = CardNumber.create("4111 1111 1111 1111").getOrThrow()
        val mc = CardNumber.create("5105 1051 0510 5100").getOrThrow()
        val amex = CardNumber.create("378282246310005").getOrThrow()
        assertEquals("VISA", visa.brand)
        assertEquals("MASTERCARD", mc.brand)
        assertEquals("AMEX", amex.brand)
    }
}
