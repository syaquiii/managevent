package com.managevent.app.features.wishlist.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.wishlist.data.WishlistRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: WishlistRepository = WishlistRepository()
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val wishlistState: StateFlow<UiState<List<EventModel>>> = _wishlistState.asStateFlow()

    var selectedCategory by mutableStateOf("Semua")
        private set

    private var allWishlistEvents: List<EventModel> = emptyList()

    val totalCount: Int
        get() = allWishlistEvents.size

    val categories = listOf("Semua", "Konser Musik", "Pernikahan", "Festival", "Pameran")

    fun fetchWishlist(userIdInput: String = "") {
        viewModelScope.launch {
            _wishlistState.value = UiState.Loading
            try {
                val activeUid = userIdInput.ifBlank {
                    SupabaseClientProvider.client.auth.currentUserOrNull()?.id.orEmpty()
                }

                val result = repository.getWishlistEvents(activeUid)
                if (result is UiState.Success) {
                    allWishlistEvents = result.data
                    applyFilter()
                } else if (result is UiState.Error) {
                    _wishlistState.value = UiState.Error(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _wishlistState.value = UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (selectedCategory == "Semua") {
            allWishlistEvents
        } else {
            allWishlistEvents.filter {
                it.category.equals(selectedCategory, ignoreCase = true) ||
                        (selectedCategory == "Konser Musik" && it.category.contains("Konser", ignoreCase = true))
            }
        }
        _wishlistState.value = UiState.Success(filtered)
    }

    fun removeFromWishlist(
        eventId: String,
        userIdInput: String = "",
        onFeedback: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val activeUid = userIdInput.ifBlank {
                    SupabaseClientProvider.client.auth.currentUserOrNull()?.id.orEmpty()
                }
                allWishlistEvents = allWishlistEvents.filterNot { it.id == eventId }
                applyFilter()
                onFeedback("Dihapus dari Favorit")
                repository.removeFromWishlist(activeUid, eventId)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Ignore
            }
        }
    }
}
