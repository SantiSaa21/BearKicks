package com.bearkicks.app.ui.strings

import org.junit.Assert.assertEquals
import org.junit.Test
import com.bearkicks.app.core.errors.ErrorKey
import com.bearkicks.app.R

class ErrorTextMappingTest {
    @Test
    fun `cart empty maps to resource id`() {
        assertEquals(R.string.error_cart_empty, errorTextRes(ErrorKey.CART_EMPTY))
    }

    @Test
    fun `card number luhn invalid maps to resource id`() {
        assertEquals(R.string.error_card_number_luhn, errorTextRes(ErrorKey.CARD_NUMBER_LUHN_INVALID))
    }
}
