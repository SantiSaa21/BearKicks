package com.bearkicks.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.home.domain.usecase.GetNewShoesUseCase
import com.bearkicks.app.features.home.domain.usecase.GetOfferShoesUseCase
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data class Success(val offers: List<ShoeModel>, val news: List<ShoeModel>) : HomeUiState()
}

class HomeViewModel(
    private val getOffers: GetOfferShoesUseCase,
    private val getNews: GetNewShoesUseCase,
    private val toggleFavorite: ToggleWishListUseCase,
    private val wishRepo: IWishListRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val offersFlow = combine(getOffers(), wishRepo.observeFavoriteIds()) { list, favIds ->
                list.map { it.copy(isLiked = it.id in favIds) }
            }
            val newsFlow = combine(getNews(), wishRepo.observeFavoriteIds()) { list, favIds ->
                list.map { it.copy(isLiked = it.id in favIds) }
            }
            combine(offersFlow, newsFlow) { offers, news ->
                val offerIds = offers.map { it.id }.toSet()
                HomeUiState.Success(offers, news.filter { it.id !in offerIds })
            }.catch { e ->
                _state.value = HomeUiState.Error(e.message ?: "Error")
            }.collectLatest { ui -> _state.value = ui }
        }
    }

    fun onToggleLike(item: ShoeModel) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) return
        viewModelScope.launch(Dispatchers.IO) { toggleFavorite(item) }
    }
}
