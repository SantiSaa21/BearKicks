package com.bearkicks.app.features.cart.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrderItemsUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrdersUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrderPaymentUseCase
import com.bearkicks.app.features.cart.domain.usecase.DeleteOrderUseCase
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(onBack: () -> Unit) {
    val observeOrders: ObserveOrdersUseCase = koinInject()
    val orders by observeOrders().collectAsState(initial = emptyList())
    val observePayment: ObserveOrderPaymentUseCase = koinInject()
    val deleteOrder: DeleteOrderUseCase = koinInject()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.bearkicks.app.R.string.orders_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = com.bearkicks.app.R.string.orders_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val fmt = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders, key = { it.orderId }) { order ->
                        val payment by observePayment(order.orderId).collectAsState(initial = null)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.weight(1f)) {
                                        Text(stringResource(id = com.bearkicks.app.R.string.order_id_prefix, order.orderId.takeLast(6).uppercase()), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(fmt.format(Date(order.createdAt)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(stringResource(id = com.bearkicks.app.R.string.price_bob, "%.2f".format(order.total)), style = MaterialTheme.typography.titleMedium)
                                }
                                payment?.let {
                                    val label = when (it.method) {
                                        "CARD" -> stringResource(id = com.bearkicks.app.R.string.payment_card_mask, it.last4 ?: "----")
                                        "QR" -> stringResource(id = com.bearkicks.app.R.string.payment_qr_hash, it.payloadHash?.take(8) ?: "")
                                        else -> it.method
                                    }
                                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                OrderItemsStrip(orderId = order.orderId)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = { scope.launch { deleteOrder(order.orderId) } }) { Text(stringResource(id = com.bearkicks.app.R.string.common_delete)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemsStrip(orderId: String) {
    val observeOrderItems: ObserveOrderItemsUseCase = koinInject()
    val items by observeOrderItems(orderId).collectAsState(initial = emptyList())
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items.take(4).forEach { it ->
                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = it.name,
                    modifier = Modifier.height(56.dp).weight(1f),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(stringResource(id = com.bearkicks.app.R.string.items_count, items.sumOf { it.qty }), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Divider()
        items.take(2).forEach { it ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(it.name, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stringResource(id = com.bearkicks.app.R.string.quantity_prefixed, it.qty))
                Text(stringResource(id = com.bearkicks.app.R.string.price_bob, "%.2f".format(it.price * it.qty)))
            }
        }
        if (items.size > 2) {
            Text(stringResource(id = com.bearkicks.app.R.string.and_more, items.size - 2), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
