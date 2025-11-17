package com.bearkicks.app.features.shop.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.bearkicks.app.features.home.domain.model.ShoeModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShoeDetailScreen(
    shoeId: String,
    onBack: () -> Unit,
    onAddToCartNavigate: () -> Unit,
    isLoggedIn: Boolean,
    onRequireLogin: () -> Unit,
    vm: ShoeDetailViewModel = koinViewModel(parameters = { parametersOf(shoeId) })
) {
    val item = vm.item.collectAsState().value
    val inCart by vm.isInCart.collectAsState(initial = false)
    val selectedSize by vm.selectedSizeFlow.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = item?.name ?: "Detalle") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } },
                actions = {
                    if (item != null) {
                        IconButton(onClick = {
                            if (!isLoggedIn) onRequireLogin() else vm.onToggleLike()
                        }) {
                            if (item.isLiked) Icon(Icons.Filled.Favorite, null)
                            else Icon(Icons.Outlined.FavoriteBorder, null)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (item != null) {
            ShoeDetailBody(
                modifier = Modifier.padding(padding),
                item = item,
                inCart = inCart,
                selectedSize = selectedSize,
                onSelectSize = vm::onSelectSize,
                onToggle = {
                    if (!isLoggedIn) onRequireLogin() else vm.onToggleLike()
                },
                onAddToCart = {
                    if (!isLoggedIn) onRequireLogin() else vm.onAddToCart(onDone = onAddToCartNavigate)
                }
            )
        } else {
            Box(modifier = Modifier.padding(padding).fillMaxSize())
        }
    }
}

@Composable
private fun ShoeDetailBody(
    modifier: Modifier,
    item: ShoeModel,
    inCart: Boolean,
    selectedSize: Int?,
    onSelectSize: (Int) -> Unit,
    onToggle: () -> Unit,
    onAddToCart: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
            contentDescription = item.name,
            modifier = Modifier.fillMaxWidth().height(260.dp),
            contentScale = ContentScale.Crop
        )
        Text(text = item.name, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
        if (!item.brand.isNullOrBlank()) {
            Text(text = item.brand ?: "", style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Bs ${"%.2f".format(item.price)}", style = MaterialTheme.typography.titleMedium)
            item.discountPrice?.let { Text(text = "Oferta: Bs ${"%.2f".format(it)}", style = MaterialTheme.typography.bodyMedium) }
        }
        item.sizes?.takeIf { it.isNotEmpty() }?.let { sizes ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Talla", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sizes.sorted().forEach { size ->
                        val selected = selectedSize == size
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectSize(size) },
                            label = { Text(size.toString()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onToggle) { Text(if (item.isLiked) "Quitar de Favoritos" else "Agregar a Favoritos") }
            val requiresSize = !item.sizes.isNullOrEmpty()
            val canAdd = !inCart && (!requiresSize || selectedSize != null)
            Button(onClick = onAddToCart, enabled = canAdd) { Text(if (inCart) "En el carrito" else "Añadir al carrito") }
        }
    }
}
