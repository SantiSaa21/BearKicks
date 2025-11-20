package com.bearkicks.app.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.cart.domain.model.CartItem
import com.bearkicks.app.features.cart.domain.usecase.ObserveCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.PlaceOrderUseCase
import com.bearkicks.app.features.cart.domain.usecase.RemoveFromCartUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    observe: ObserveCartUseCase,
    private val remove: RemoveFromCartUseCase,
    private val placeOrder: PlaceOrderUseCase
) : ViewModel() {
    val items: StateFlow<List<CartItem>> = observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val total: StateFlow<Double> = items.map { it.sumOf { it.subtotal } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    private val _error = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onRemove(id: String) { viewModelScope.launch { remove(id) } }
    fun onSimulatedCheckout(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (items.value.isEmpty()) {
                    _error.value = com.bearkicks.app.core.errors.ErrorKey.CART_EMPTY.name
                    return@launch
                }
                val id = placeOrder(items.value)
                onSuccess(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
