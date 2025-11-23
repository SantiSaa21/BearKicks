package com.bearkicks.application.ui.strings

import org.junit.Assert.assertEquals
import org.junit.Test
import com.bearkicks.application.core.errors.ErrorKey
import com.bearkicks.application.R

class ErrorTextMappingTest {
    @Test
    fun `cart empty maps to resource id`() {
        assertEquals(R.string.error_cart_empty, errorTextRes(ErrorKey.CART_EMPTY))
    }

    // Payment error keys removed; no longer tested.
}
