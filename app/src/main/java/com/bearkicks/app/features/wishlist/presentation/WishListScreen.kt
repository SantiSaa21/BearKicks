package com.bearkicks.app.features.wishlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import com.bearkicks.app.ui.components.BKProductCard
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.getValue

@Composable
fun WishListScreen(
    onProductClick: (ShoeModel) -> Unit = {}
) {
    val viewModel: WishListViewModel = koinViewModel()
    val favorites = viewModel.favorites.collectAsState(initial = emptyList()).value
    val observeAuth: ObserveAuthStateUseCase = koinInject()
    val currentUser by observeAuth().collectAsState(initial = null)

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no tienes favoritos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(favorites, key = { it.id }) { shoe ->
            BKProductCard(
                shoe = shoe.copy(isLiked = true),
                onClick = onProductClick,
                onLikeClick = { viewModel.onToggle(it) },
                isLoggedIn = currentUser != null
            )
        }
    }
}
