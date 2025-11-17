package com.bearkicks.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BKElevations(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp
)

val LocalBKElevations = staticCompositionLocalOf { BKElevations() }

@Composable
fun ProvideBKElevations(elevations: BKElevations = BKElevations(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBKElevations provides elevations, content = content)
}