package com.bearkicks.app.features.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.domain.usecase.GetAllShoesUseCase
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class ShopUiState(
    val query: String = "",
    val items: List<ShoeModel> = emptyList(),
    val filtered: List<ShoeModel> = emptyList()
)

class ShopViewModel(
    private val getAllShoesUseCase: GetAllShoesUseCase,
    private val toggleWishListUseCase: ToggleWishListUseCase,
    private val wishListRepository: IWishListRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val state: StateFlow<ShopUiState> =
        combine(
            _query,
            combine(getAllShoesUseCase(), wishListRepository.observeFavoriteIds()) { shoes, favoriteIds ->
                shoes.map { it.copy(isLiked = it.id in favoriteIds) }
            }
        ) { query, items ->
            val filtered = if (query.isBlank()) items else items.filter {
                it.name.contains(query, true) || (it.brand?.contains(query, true) == true)
            }
            ShopUiState(query = query, items = items, filtered = filtered)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ShopUiState())

    fun onQueryChange(query: String) { _query.value = query }

    fun onToggleLike(item: ShoeModel) {
        if (FirebaseAuth.getInstance().currentUser == null) return
        viewModelScope.launch(Dispatchers.IO) { toggleWishListUseCase(item) }
    }
}
