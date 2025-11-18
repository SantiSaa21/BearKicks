package com.bearkicks.app.features.cart.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onCheckoutSuccess: (String) -> Unit) {
    val viewModel: CartViewModel = koinViewModel()
    val items = viewModel.items.collectAsState().value
    val total = viewModel.total.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val qrHash = viewModel.lastQrHash.collectAsState().value
    val showSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.matchParentSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { item ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).height(64.dp).aspectRatio(1f)
                )
                Column(Modifier.weight(1f)) {
                    Text(item.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                    item.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(id = com.bearkicks.app.R.string.cart_size_label, item.size ?: "-"))
                        Text(stringResource(id = com.bearkicks.app.R.string.quantity_prefixed, item.qty))
                        Text(stringResource(id = com.bearkicks.app.R.string.price_bob, String.format("%.2f", item.subtotal)), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Divider()
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(id = com.bearkicks.app.R.string.cart_total), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(id = com.bearkicks.app.R.string.price_bob, String.format("%.2f", total)), style = MaterialTheme.typography.titleMedium)
        }
        if (error!=null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Button(onClick = { showSheet.value = true }, enabled = items.isNotEmpty(), modifier = Modifier.fillMaxWidth()) { Text(stringResource(id = com.bearkicks.app.R.string.checkout_pay)) }
    }
        if (showSheet.value) {
            ModalBottomSheet(onDismissRequest = { showSheet.value = false }, sheetState = sheetState) {
                CheckoutBottomSheet(
                    items = items,
                    onDismiss = { showSheet.value = false },
                    onPayCard = { n,e,c,h -> viewModel.onCheckoutCard(n,e,c,h) { id -> showSheet.value=false; onCheckoutSuccess(id) } },
                    onPayQr = { provider, ts -> viewModel.onCheckoutQr(provider, ts) { id -> showSheet.value=false; onCheckoutSuccess(id) } }
                )
                qrHash?.let { Text(stringResource(id = com.bearkicks.app.R.string.qr_hash_label, it), style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}
