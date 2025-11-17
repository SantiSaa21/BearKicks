package com.bearkicks.app.features.cart.presentation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CardNumberSpacingVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawDigits = text.text.filter { it.isDigit() }
        val builder = StringBuilder()
        val spacePositions = mutableListOf<Int>()
        val originalToTransformed = IntArray(rawDigits.length + 1)
        var transformedIndex = 0
        var originalIndex = 0
        while (originalIndex < rawDigits.length) {
            if (originalIndex > 0 && originalIndex % 4 == 0) {
                builder.append(' ')
                spacePositions.add(transformedIndex)
                transformedIndex++
            }
            builder.append(rawDigits[originalIndex])
            originalToTransformed[originalIndex] = transformedIndex
            transformedIndex++
            originalIndex++
        }
        originalToTransformed[rawDigits.length] = transformedIndex // end position
        val grouped = builder.toString()
        val out = AnnotatedString(grouped)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return originalToTransformed[offset.coerceIn(0, rawDigits.length)]
            }
            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, grouped.length)
                // count spaces strictly before this transformed offset
                val spacesBefore = spacePositions.count { it < clamped }
                val original = clamped - spacesBefore
                return original.coerceIn(0, rawDigits.length)
            }
        }
        return TransformedText(out, offsetMapping)
    }
}

fun groupCardDigits(digits: String): String = digits.filter { it.isDigit() }.chunked(4).joinToString(" ")
