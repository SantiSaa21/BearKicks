package com.bearkicks.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bearkicks.app.ui.theme.BKTypography

enum class BKButtonSize { Small, Medium, Large }

private fun BKButtonMinHeight(size: BKButtonSize) = when (size) {
    BKButtonSize.Small -> 44.dp
    BKButtonSize.Medium -> 52.dp
    BKButtonSize.Large -> 56.dp
}
private fun BKButtonContentPadding(size: BKButtonSize): PaddingValues = when (size) {
    BKButtonSize.Small -> PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    BKButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 0.dp)
    BKButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 0.dp)
}
private fun BKButtonShape() = RoundedCornerShape(12.dp)
private fun BKButtonTextStyle(size: BKButtonSize): TextStyle = when (size) {
    BKButtonSize.Small -> BKTypography.labelLarge
    BKButtonSize.Medium -> BKTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
    BKButtonSize.Large -> BKTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
}
private val primaryColors: ButtonColors
    @Composable get() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )
private val tonalColors: ButtonColors
    @Composable get() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
private val textColors: ButtonColors
    @Composable get() = ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
@Composable
fun BKButton(
    text: String,
    modifier: Modifier = Modifier,
    size: BKButtonSize = BKButtonSize.Medium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val disabledBorder = BorderStroke(1.dp, SolidColor(MaterialTheme.colorScheme.primary))
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = BKButtonMinHeight(size)),
        enabled = enabled,
        shape = BKButtonShape(),
        contentPadding = BKButtonContentPadding(size),
        border = if (!enabled) disabledBorder else null,
        colors = primaryColors
    ) {
        Text(text, style = BKButtonTextStyle(size))
    }
}
@Composable
fun BKTonalButton(
    text: String,
    modifier: Modifier = Modifier,
    size: BKButtonSize = BKButtonSize.Medium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = BKButtonMinHeight(size)),
        enabled = enabled,
        shape = BKButtonShape(),
        contentPadding = BKButtonContentPadding(size),
        colors = tonalColors
    ) {
        Text(text, style = BKButtonTextStyle(size))
    }
}
@Composable
fun BKOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    size: BKButtonSize = BKButtonSize.Medium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val border = BorderStroke(1.dp, SolidColor(MaterialTheme.colorScheme.outline))
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = BKButtonMinHeight(size)),
        enabled = enabled,
        shape = BKButtonShape(),
        contentPadding = BKButtonContentPadding(size),
        border = border,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text, style = BKButtonTextStyle(size))
    }
}
@Composable
fun BKTextButton(
    text: String,
    modifier: Modifier = Modifier,
    size: BKButtonSize = BKButtonSize.Medium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = BKButtonMinHeight(size)),
        enabled = enabled,
        shape = BKButtonShape(),
        colors = textColors
    ) {
        val base = when (size) {
            BKButtonSize.Small -> 12.sp
            BKButtonSize.Medium -> 14.sp
            BKButtonSize.Large -> 16.sp
        }
        Text(text = text, style = BKButtonTextStyle(size).copy(fontSize = base))
    }
}