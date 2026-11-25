package com.managevent.app.features.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.data.KatalogRepository
import com.managevent.app.features.wishlist.data.WishlistRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: WishlistRepository = WishlistRepository(),
    private val katalogRepository: KatalogRepository = KatalogRepository()
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val wishlistState: StateFlow<UiState<List<EventModel>>> = _wishlistState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<EventModel>>(UiState.Idle)
    val detailState: StateFlow<UiState<EventModel>> = _detailState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private var allWishlistEvents: List<EventModel> = emptyList()

    val categories = listOf("Semua", "Pernikahan", "Konser", "Seminar", "Ulang Tahun", "Festival", "Pameran")

    fun fetchWishlist(userIdInput: String = "") {
        viewModelScope.launch {
            _wishlistState.update { UiState.Loading }
            try {
                if (userIdInput.isBlank()) {
                    allWishlistEvents = emptyList()
                    _totalCount.update { 0 }
                    _wishlistState.update { UiState.Success(emptyList()) }
                    return@launch
                }

                when (val result = repository.getWishlistEvents(userIdInput)) {
                    is UiState.Success -> {
                        allWishlistEvents = result.data
                        _totalCount.update { allWishlistEvents.size }
                        applyFilter()
                    }
                    is UiState.Error -> {
                        _wishlistState.update { UiState.Error(result.message) }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _wishlistState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun fetchWishlistDetail(eventId: String) {
        viewModelScope.launch {
            _detailState.update { UiState.Loading }
            try {
                val localFound = allWishlistEvents.find { it.id == eventId }
                if (localFound != null) {
                    _detailState.update { UiState.Success(localFound) }
                } else {
                    when (val result = katalogRepository.getEventById(eventId)) {
                        is UiState.Success -> _detailState.update { UiState.Success(result.data.copy(isFavorite = true)) }
                        is UiState.Error -> _detailState.update { result }
                        else -> {}
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.update { category }
        applyFilter()
    }

    private fun applyFilter() {
        val selected = _selectedCategory.value
        val filtered = if (selected == "Semua") {
            allWishlistEvents
        } else {
            allWishlistEvents.filter { it.category.equals(selected, ignoreCase = true) }
        }
        _wishlistState.update { UiState.Success(filtered) }
    }

    fun removeFromWishlist(
        eventId: String,
        userIdInput: String = "",
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val previousEvents = allWishlistEvents
        allWishlistEvents = allWishlistEvents.filterNot { it.id == eventId }
        _totalCount.update { allWishlistEvents.size }
        applyFilter()

        viewModelScope.launch {
            try {
                when (val result = repository.removeFromWishlist(userIdInput, eventId)) {
                    is UiState.Success -> onSuccess(result.data)
                    is UiState.Error -> {
                        allWishlistEvents = previousEvents
                        _totalCount.update { allWishlistEvents.size }
                        applyFilter()
                        onError(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                allWishlistEvents = previousEvents
                _totalCount.update { allWishlistEvents.size }
                applyFilter()
                onError(e.toUserFriendlyMessage())
            }
        }
    }
}
