package com.bearkicks.app.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.domain.usecase.ObserveCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.PlaceOrderUseCase
import com.bearkicks.app.features.cart.domain.usecase.RemoveFromCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.CheckoutWithCardUseCase
import com.bearkicks.app.features.cart.domain.usecase.CheckoutWithQrUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    observe: ObserveCartUseCase,
    private val remove: RemoveFromCartUseCase,
    private val placeOrder: PlaceOrderUseCase,
    private val checkoutCard: CheckoutWithCardUseCase,
    private val checkoutQr: CheckoutWithQrUseCase
) : ViewModel() {
    val items: StateFlow<List<CartItem>> = observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val total: StateFlow<Double> = items.map { it.sumOf { it.subtotal } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    private val _error = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _lastQrHash = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val lastQrHash: StateFlow<String?> = _lastQrHash

    fun onRemove(id: String) { viewModelScope.launch { remove(id) } }
    fun onCheckoutCard(number: String, expiry: String, cvv: String, holder: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            checkoutCard(items.value, number, expiry, cvv, holder)
                .onSuccess { onSuccess(it) }
                .onFailure { _error.value = it.message }
        }
    }
    fun onCheckoutQr(provider: String, timestamp: Long, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            checkoutQr(items.value, provider, timestamp)
                .onSuccess { (id, hash) -> _lastQrHash.value = hash; onSuccess(id) }
                .onFailure { _error.value = it.message }
        }
    }
}
