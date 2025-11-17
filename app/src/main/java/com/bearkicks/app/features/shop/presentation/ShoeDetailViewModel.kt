package com.bearkicks.app.features.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.domain.usecase.GetShoeDetailUseCase
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import com.bearkicks.app.features.cart.domain.usecase.AddToCartUseCase
import com.bearkicks.app.features.cart.domain.usecase.ObserveIsInCartUseCase
import com.bearkicks.app.features.cart.domain.model.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoeDetailViewModel(
    private val id: String,
    private val getDetail: GetShoeDetailUseCase,
    private val toggleFavorite: ToggleWishListUseCase,
    private val wishRepo: IWishListRepository,
    private val addToCart: AddToCartUseCase,
    observeInCart: ObserveIsInCartUseCase
) : ViewModel() {
    private val _item = MutableStateFlow<ShoeModel?>(null)
    val item: StateFlow<ShoeModel?> = _item.asStateFlow()
    private val selectedSize = MutableStateFlow<Int?>(null)
    val selectedSizeFlow: StateFlow<Int?> = selectedSize.asStateFlow()

    val isInCart: Flow<Boolean> = selectedSize
        .flatMapLatest { size ->
            if (size != null) observeInCart(id, size) else observeInCart(id)
        }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(getDetail(id), wishRepo.observeIsFavorite(id)) { shoe, favored ->
                shoe?.copy(isLiked = favored)
            }.collect { _item.value = it }
        }
    }

    fun onSelectSize(size: Int) { selectedSize.value = size }

    fun onToggleLike() {
        val currentShoe = _item.value ?: return
        viewModelScope.launch(Dispatchers.IO) { toggleFavorite(currentShoe) }
    }

    fun onAddToCart(qty: Int = 1, size: Int? = null, onDone: (() -> Unit)? = null) {
        val currentShoe = _item.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val effectiveSize = size ?: selectedSize.value
            if (!currentShoe.sizes.isNullOrEmpty() && effectiveSize == null) return@launch
            val cartItem = CartItem(
                id = "",
                shoeId = currentShoe.id,
                name = currentShoe.name,
                brand = currentShoe.brand,
                price = currentShoe.discountPrice ?: currentShoe.price,
                imageUrl = currentShoe.imageUrl,
                size = effectiveSize,
                qty = qty
            )
            addToCart(cartItem)
            if (onDone != null) withContext(Dispatchers.Main) { onDone() }
        }
    }
}
