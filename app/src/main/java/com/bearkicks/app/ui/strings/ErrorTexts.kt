package com.bearkicks.app.ui.strings

import androidx.annotation.StringRes
import com.bearkicks.app.R
import com.bearkicks.app.core.errors.ErrorKey

@StringRes
fun errorTextRes(key: ErrorKey): Int = when (key) {
    // Auth/session
    ErrorKey.NOT_AUTHENTICATED -> R.string.error_not_authenticated
    ErrorKey.USER_MISSING_EMAIL -> R.string.error_user_missing_email
    ErrorKey.PASSWORDS_DO_NOT_MATCH -> R.string.error_passwords_do_not_match
    ErrorKey.WRONG_CURRENT_PASSWORD -> R.string.error_wrong_current_password
    ErrorKey.TOO_MANY_ATTEMPTS -> R.string.error_too_many_attempts
    ErrorKey.CHANGE_PASSWORD_ERROR -> R.string.error_change_password_generic

    // Validation
    ErrorKey.INVALID_FIRST_NAME_RULES -> R.string.error_invalid_first_name_rules
    ErrorKey.INVALID_LAST_NAME_RULES -> R.string.error_invalid_last_name_rules
    ErrorKey.INVALID_USERNAME_RULES -> R.string.error_invalid_username_rules
    ErrorKey.INVALID_EMAIL -> R.string.error_invalid_email
    ErrorKey.INVALID_PHONE_RULES -> R.string.error_invalid_phone_rules
    ErrorKey.INVALID_ADDRESS_RULES -> R.string.error_invalid_address_rules
    ErrorKey.MUST_BE_ADULT -> R.string.error_must_be_adult
    ErrorKey.INVALID_PASSWORD_RULES -> R.string.error_invalid_password_rules

    // Media
    ErrorKey.IMAGE_READ_ERROR -> R.string.error_image_read
    ErrorKey.IMAGE_OPEN_ERROR -> R.string.error_image_open

    // Cart / checkout
    ErrorKey.CART_EMPTY -> R.string.error_cart_empty

    // Card/payment
    ErrorKey.CARD_NUMBER_INVALID_LENGTH -> R.string.error_card_number_length
    ErrorKey.CARD_NUMBER_LUHN_INVALID -> R.string.error_card_number_luhn
    ErrorKey.EXPIRY_FORMAT_INVALID -> R.string.error_expiry_format
    ErrorKey.EXPIRY_EXPIRED -> R.string.error_expiry_expired
    ErrorKey.CVV_INVALID -> R.string.error_cvv_invalid
    ErrorKey.CARDHOLDER_INVALID -> R.string.error_cardholder_invalid

    // Generic fallback
    ErrorKey.GENERIC_ERROR -> R.string.error_generic
}
