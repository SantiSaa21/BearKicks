package com.bearkicks.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BKBrandPrimary,
    onPrimary = BKNeutral100,
    primaryContainer = BKBrandPrimaryLight,
    onPrimaryContainer = BKNeutral100,
    secondary = BKBrandSecondary,
    onSecondary = BKNeutral10,
    tertiary = BKBrandAccent,
    onTertiary = BKNeutral100,
    background = BKBackgroundLight,
    onBackground = BKOnBackgroundLight,
    surface = BKSurfaceLight,
    onSurface = BKOnSurfaceLight,
    surfaceVariant = BKSurfaceVariantLight,
    onSurfaceVariant = BKOnSurfaceVariantLight,
    outline = BKOutline,
    outlineVariant = BKOutlineVariant,
    error = BKError,
    scrim = BKScrim
)

private val DarkColors = darkColorScheme(
    primary = BKBrandPrimaryLight,
    onPrimary = BKNeutral10,
    primaryContainer = BKBrandPrimary,
    onPrimaryContainer = BKNeutral100,
    secondary = BKBrandSecondary,
    onSecondary = BKNeutral10,
    tertiary = BKBrandAccent,
    onTertiary = BKNeutral100,
    background = BKBackgroundDark,
    onBackground = BKOnBackgroundDark,
    surface = BKSurfaceDark,
    onSurface = BKOnSurfaceDark,
    surfaceVariant = BKSurfaceVariantDark,
    onSurfaceVariant = BKOnSurfaceVariantDark,
    outline = BKOutline,
    outlineVariant = BKOutlineVariant,
    error = BKError,
    scrim = BKScrim
)

@Composable
fun BearKicksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    spacing: BKSpacing = BKSpacing(),
    elevations: BKElevations = BKElevations(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    ProvideBKSpacing(spacing) {
        ProvideBKElevations(elevations) {
            MaterialTheme(
                colorScheme = colors,
                typography = BKTypography,
                shapes = BKShapes,
                content = content
            )
        }
    }
}

object BKTheme {
    val spacing: BKSpacing
        @Composable get() = LocalBKSpacing.current
    val elevations: BKElevations
        @Composable get() = LocalBKElevations.current
    val colors
        @Composable get() = MaterialTheme.colorScheme
    val typography
        @Composable get() = MaterialTheme.typography
    val shapes
        @Composable get() = MaterialTheme.shapes
}