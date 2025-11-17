package com.bearkicks.app.features.auth.domain.util

import java.util.Locale

private fun String.capitalizeToken(): String {
    if (isEmpty()) return this
    return lowercase(Locale.getDefault()).replaceFirstChar { c ->
        if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString()
    }
}

fun normalizeFirstName(raw: String): String {
    val trimmed = raw.trim()
    return trimmed.capitalizeToken()
}

fun normalizeLastName(raw: String): String {
    return raw.trim().split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { it.capitalizeToken() }
}

fun normalizeAddress(raw: String): String {
    return raw.trim().split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { token ->
            if (token.firstOrNull()?.isLetter() == true) token.capitalizeToken() else token
        }
}
