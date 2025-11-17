package com.bearkicks.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bearkicks.app.ui.theme.BKTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BKTopBar(
    title: String,
    modifier: Modifier = Modifier,
    centered: Boolean = true,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
    if (centered) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = { Text(text = title, style = BKTypography.titleLarge) },
            navigationIcon = { navigationIcon?.invoke() },
            actions = { actions?.invoke(this) },
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            modifier = modifier,
            title = { Text(text = title, style = BKTypography.titleLarge) },
            navigationIcon = { navigationIcon?.invoke() },
            actions = { actions?.invoke(this) },
            colors = colors,
            scrollBehavior = scrollBehavior
        )
    }
}