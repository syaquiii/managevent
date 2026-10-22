package com.managevent.app.features.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.wishlist.data.WishlistModel
import com.managevent.app.features.wishlist.data.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: WishlistRepository = WishlistRepository()
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<UiState<List<WishlistModel>>>(UiState.Idle)
    val wishlistState: StateFlow<UiState<List<WishlistModel>>> = _wishlistState.asStateFlow()

    fun fetchWishlist(userId: String) {
        viewModelScope.launch {
            _wishlistState.value = UiState.Loading
            _wishlistState.value = repository.getWishlist(userId)
        }
    }
}
