package com.bearkicks.app.features.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.domain.usecase.ObserveWishListUseCase
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishListViewModel(
    observe: ObserveWishListUseCase,
    private val toggle: ToggleWishListUseCase
) : ViewModel() {
    val favorites: StateFlow<List<ShoeModel>> =
        observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun onToggle(item: ShoeModel) {
        viewModelScope.launch { toggle(item) }
    }
}