package com.bearkicks.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bearkicks.app.features.home.domain.model.ShoeModel

@Composable
fun BKCarousel(
    title: String,
    items: List<ShoeModel>,
    onLikeClick: (ShoeModel) -> Unit,
    onProductClick: (ShoeModel) -> Unit,
    isLoggedIn: Boolean = true,
    onRequireLogin: () -> Unit = {},
    onSeeAll: (() -> Unit)? = null,
    cardWidth: Dp = 260.dp
) {
    BKSection(
        title = title,
        actionText = if (onSeeAll != null) "Ver todo" else null,
        onAction = onSeeAll
    )
    Spacer(Modifier.height(8.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { shoe ->
            BKProductCard(
                shoe = shoe,
                onLikeClick = onLikeClick,
                isLoggedIn = isLoggedIn,
                onRequireLogin = onRequireLogin,
                onClick = onProductClick,
                modifier = Modifier.width(cardWidth)
            )
        }
    }
}
