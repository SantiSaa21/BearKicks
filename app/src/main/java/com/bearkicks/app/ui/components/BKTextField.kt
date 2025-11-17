package com.bearkicks.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.bearkicks.app.ui.theme.BKTypography

enum class BKTextFieldVariant { Filled, Outlined }
enum class BKTextFieldSize { Small, Medium, Large }
private fun BKTextStyle(size: BKTextFieldSize): TextStyle = when (size) {
    BKTextFieldSize.Small -> BKTypography.bodyMedium
    BKTextFieldSize.Medium -> BKTypography.bodyLarge
    BKTextFieldSize.Large -> BKTypography.titleSmall
}
private fun BKMinHeight(size: BKTextFieldSize) = when (size) {
    BKTextFieldSize.Small -> 44.dp
    BKTextFieldSize.Medium -> 52.dp
    BKTextFieldSize.Large -> 60.dp
}
@Composable
fun BKTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    errorText: String? = null,
    size: BKTextFieldSize = BKTextFieldSize.Medium,
    variant: BKTextFieldVariant = BKTextFieldVariant.Outlined,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
    )
) {
    val showPassword: MutableState<Boolean> = remember { mutableStateOf(false) }
    val visual: VisualTransformation =
        if (isPassword && !showPassword.value) PasswordVisualTransformation() else VisualTransformation.None

    val tfModifier = modifier.fillMaxWidth().heightIn(min = BKMinHeight(size))

    val outlinedColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        cursorColor = MaterialTheme.colorScheme.primary
    )
    val filledColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    val trailingContent: @Composable (() -> Unit)? =
        trailing ?: if (isPassword) {
            {
                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        imageVector = if (showPassword.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        } else null

    val labelComposable: (@Composable () -> Unit)? = label?.let { { Text(it, style = BKTypography.labelLarge) } }
    val placeholderComposable: (@Composable () -> Unit)? = placeholder?.let { { Text(it, style = BKTextStyle(size)) } }

    if (variant == BKTextFieldVariant.Outlined) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = tfModifier,
            enabled = enabled,
            textStyle = BKTextStyle(size),
            label = labelComposable,
            placeholder = placeholderComposable,
            leadingIcon = leading,
            trailingIcon = trailingContent,
            singleLine = singleLine,
            maxLines = maxLines,
            isError = errorText != null,
            visualTransformation = visual,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = outlinedColors
        )
        if (errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = BKTypography.labelSmall
            )
        }
    } else {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = tfModifier,
            enabled = enabled,
            textStyle = BKTextStyle(size),
            label = labelComposable,
            placeholder = placeholderComposable,
            leadingIcon = leading,
            trailingIcon = trailingContent,
            singleLine = singleLine,
            maxLines = maxLines,
            isError = errorText != null,
            visualTransformation = visual,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = filledColors
        )
        if (errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = BKTypography.labelSmall
            )
        }
    }
}