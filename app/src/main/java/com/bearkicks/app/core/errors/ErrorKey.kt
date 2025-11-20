package com.bearkicks.app.core.errors

/**
 * Stable error identifiers produced by domain/use cases.
 * UI maps these to localized strings.
 */
enum class ErrorKey {
    // Auth/session
    NOT_AUTHENTICATED,
    USER_MISSING_EMAIL,
    PASSWORDS_DO_NOT_MATCH,
    WRONG_CURRENT_PASSWORD,
    TOO_MANY_ATTEMPTS,
    CHANGE_PASSWORD_ERROR,

    // Validation
    INVALID_FIRST_NAME_RULES,
    INVALID_LAST_NAME_RULES,
    INVALID_USERNAME_RULES,
    INVALID_EMAIL,
    INVALID_PHONE_RULES,
    INVALID_ADDRESS_RULES,
    MUST_BE_ADULT,
    INVALID_PASSWORD_RULES,

    // Media / photo
    IMAGE_READ_ERROR,
    IMAGE_OPEN_ERROR,

    // Cart / checkout
    CART_EMPTY,

    // (Payment validation removed)

    // Generic fallback
    GENERIC_ERROR,
}

class DomainException(val key: ErrorKey, cause: Throwable? = null) : RuntimeException(key.name, cause)
