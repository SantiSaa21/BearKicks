package com.bearkicks.app.features.cart.presentation

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberTransformationTest {
    @Test
    fun `transformation inserts spaces`() {
        val vt = CardNumberSpacingVisualTransformation()
        val input = AnnotatedString("4111111111111111")
        val transformed = vt.filter(input)
        assertEquals("4111 1111 1111 1111", transformed.text.text)
    }

    @Test
    fun `cursor mapping forward and backward`() {
        val vt = CardNumberSpacingVisualTransformation()
        val input = AnnotatedString("411111")
        val transformed = vt.filter(input)
        // Original index 4 (after 4 digits) should map to transformed index 5 (skip space) but remain in range
        val mapFwd = transformed.offsetMapping.originalToTransformed(4)
        val mapBack = transformed.offsetMapping.transformedToOriginal(mapFwd)
        assertEquals(5, mapFwd)
        assertEquals(4, mapBack)
    }
}
