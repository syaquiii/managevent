package com.managevent.app.features.wishlist.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.wishlist.data.WishlistRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: WishlistRepository = WishlistRepository()
) : ViewModel() {

    private val _wishlistState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val wishlistState: StateFlow<UiState<List<EventModel>>> = _wishlistState.asStateFlow()

    // State Filter Kategori
    var selectedCategory by mutableStateOf("Semua")
        private set

    // List asli seluruh favorit
    private var allWishlistEvents: List<EventModel> = emptyList()

    val totalCount: Int
        get() = allWishlistEvents.size

    // Kategori yang tersedia
    val categories = listOf("Semua", "Konser Musik", "Pernikahan", "Festival", "Pameran")

    fun fetchWishlist(userId: String = "dummy-user-id") {
        viewModelScope.launch {
            _wishlistState.value = UiState.Loading
            try {
                delay(300)
                allWishlistEvents = listOf(
                    EventModel(
                        id = "evt-1",
                        title = "Nusantara Symphony Fest",
                        description = "Konser musik simfoni megah menyajikan karya maestro Nusantara.",
                        category = "Konser",
                        date = "14 Mei 2026",
                        location = "GBK, Jakarta",
                        price = 250000.0,
                        imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=600",
                        isFavorite = true
                    ),
                    EventModel(
                        id = "evt-2",
                        title = "Gelaran Nikah Adat Nusantara",
                        description = "Pameran gaun dan adat pernikahan tradisional terlengkap.",
                        category = "Pernikahan",
                        date = "22 Agu 2026",
                        location = "JCC Senayan",
                        price = 75000.0,
                        imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=600",
                        isFavorite = true
                    ),
                    EventModel(
                        id = "evt-3",
                        title = "Bandung Rasa & Kopi Fest",
                        description = "Festival kuliner dan kopi terbesar se-Jawa Barat.",
                        category = "Festival",
                        date = "10 Okt 2026",
                        location = "Braga, Bandung",
                        price = 50000.0,
                        imageUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=600",
                        isFavorite = true
                    ),
                    EventModel(
                        id = "evt-4",
                        title = "Bali Immersive Art Expo",
                        description = "Pameran seni digital imersif di Ubud Bali.",
                        category = "Pameran",
                        date = "15 Jan 2027",
                        location = "Ubud, Bali",
                        price = 180000.0,
                        imageUrl = "https://images.unsplash.com/photo-1508997449629-303059a039c0?q=80&w=600",
                        isFavorite = true
                    ),
                    EventModel(
                        id = "evt-5",
                        title = "Senja Jazz Prambanan",
                        description = "Festival musik jazz di latar keindahan Candi Prambanan.",
                        category = "Konser",
                        date = "28 Feb 2027",
                        location = "Sleman, DIY",
                        price = 350000.0,
                        imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=600",
                        isFavorite = true
                    ),
                    EventModel(
                        id = "evt-6",
                        title = "Modern Garden Wedding Fair",
                        description = "Inspirasi pernikahan konsep outdoor garden party.",
                        category = "Pernikahan",
                        date = "18 Apr 2027",
                        location = "Grand City, SBY",
                        price = 45000.0,
                        imageUrl = "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?q=80&w=600",
                        isFavorite = true
                    )
                )
                applyFilter()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _wishlistState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
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

    fun removeFromWishlist(eventId: String, userId: String = "dummy-user-id") {
        viewModelScope.launch {
            try {
                allWishlistEvents = allWishlistEvents.filterNot { it.id == eventId }
                applyFilter()
                repository.removeFromWishlist(userId, eventId)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Ignore
            }
        }
    }
}
