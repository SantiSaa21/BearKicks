package com.bearkicks.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BKSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp
)

val LocalBKSpacing = staticCompositionLocalOf { BKSpacing() }

@Composable
fun ProvideBKSpacing(spacing: BKSpacing = BKSpacing(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBKSpacing provides spacing, content = content)
}