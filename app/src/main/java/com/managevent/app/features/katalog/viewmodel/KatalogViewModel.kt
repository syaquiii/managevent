package com.managevent.app.features.katalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.data.KatalogRepository
import com.managevent.app.features.ulasan.data.UlasanRepository
import com.managevent.app.features.wishlist.data.WishlistRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class KatalogSortOption(val label: String) {
    TERBARU("Terbaru"),
    HARGA_TERENDAH("Harga Terendah"),
    HARGA_TERTINGGI("Harga Tertinggi")
}

data class EventReviewSummary(
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0
)

class KatalogViewModel(
    private val repository: KatalogRepository = KatalogRepository(),
    private val wishlistRepository: WishlistRepository = WishlistRepository(),
    private val ulasanRepository: UlasanRepository = UlasanRepository(),
    autoFetch: Boolean = true
) : ViewModel() {

    private var rawEvents: List<EventModel> = emptyList()

    private val _eventsState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val eventsState: StateFlow<UiState<List<EventModel>>> = _eventsState.asStateFlow()

    private val _eventDetailState = MutableStateFlow<UiState<EventModel>>(UiState.Idle)
    val eventDetailState: StateFlow<UiState<EventModel>> = _eventDetailState.asStateFlow()

    private val _reviewSummary = MutableStateFlow(EventReviewSummary())
    val reviewSummary: StateFlow<EventReviewSummary> = _reviewSummary.asStateFlow()

    private val _formState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val formState: StateFlow<UiState<String>> = _formState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedLocation = MutableStateFlow("Semua Lokasi")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _availableLocations = MutableStateFlow(listOf("Semua Lokasi"))
    val availableLocations: StateFlow<List<String>> = _availableLocations.asStateFlow()

    private val _sortOption = MutableStateFlow(KatalogSortOption.TERBARU)
    val sortOption: StateFlow<KatalogSortOption> = _sortOption.asStateFlow()

    init {
        if (autoFetch) {
            fetchEvents()
        }
    }

    fun fetchEvents(userId: String = "") {
        viewModelScope.launch {
            _eventsState.update { UiState.Loading }
            try {
                when (val result = repository.getEvents()) {
                    is UiState.Success -> {
                        val events = result.data
                        val favIds = if (userId.isNotBlank()) {
                            val favRes = wishlistRepository.getWishlistEvents(userId)
                            if (favRes is UiState.Success) {
                                favRes.data.map { it.id }.toSet()
                            } else {
                                emptySet()
                            }
                        } else {
                            emptySet()
                        }

                        rawEvents = events.map { event ->
                            event.copy(isFavorite = favIds.contains(event.id))
                        }
                        updateAvailableLocations()
                        applyFilter()
                    }
                    is UiState.Error -> {
                        _eventsState.update { UiState.Error(result.message) }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventsState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun fetchEventById(id: String) {
        viewModelScope.launch {
            _eventDetailState.update { UiState.Loading }
            _reviewSummary.update { EventReviewSummary() }
            try {
                val localFound = rawEvents.find { it.id == id }
                if (localFound != null) {
                    _eventDetailState.update { UiState.Success(localFound) }
                } else {
                    _eventDetailState.update { repository.getEventById(id) }
                }

                when (val reviewsResult = ulasanRepository.getUlasanByEvent(id)) {
                    is UiState.Success -> {
                        val list = reviewsResult.data
                        val avg = if (list.isNotEmpty()) list.map { it.rating }.average() else 0.0
                        _reviewSummary.update {
                            EventReviewSummary(averageRating = avg, reviewCount = list.size)
                        }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventDetailState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
        applyFilter()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.update { category }
        applyFilter()
    }

    fun onLocationSelected(location: String) {
        _selectedLocation.update { location }
        applyFilter()
    }

    fun onSortOptionSelected(option: KatalogSortOption) {
        _sortOption.update { option }
        applyFilter()
    }

    fun resetFilters() {
        _searchQuery.update { "" }
        _selectedCategory.update { "Semua" }
        _selectedLocation.update { "Semua Lokasi" }
        _sortOption.update { KatalogSortOption.TERBARU }
        applyFilter()
    }

    fun resetFormState() {
        _formState.update { UiState.Idle }
    }

    fun syncFavoriteStateLocally(eventId: String, isFavorite: Boolean) {
        rawEvents = rawEvents.map { event ->
            if (event.id == eventId) event.copy(isFavorite = isFavorite) else event
        }
        val currentDetail = (_eventDetailState.value as? UiState.Success)?.data
        if (currentDetail != null && currentDetail.id == eventId) {
            _eventDetailState.update { UiState.Success(currentDetail.copy(isFavorite = isFavorite)) }
        }
        applyFilter()
    }

    fun toggleFavorite(
        eventId: String,
        userId: String = "",
        onFeedback: (String) -> Unit = {}
    ) {
        if (userId.isBlank()) {
            onFeedback("Silakan masuk terlebih dahulu untuk menyimpan favorit.")
            return
        }

        val currentEvent = rawEvents.find { it.id == eventId }
            ?: (_eventDetailState.value as? UiState.Success)?.data?.takeIf { it.id == eventId }
        val previousState = currentEvent?.isFavorite ?: false
        val nextState = !previousState

        syncFavoriteStateLocally(eventId, nextState)

        viewModelScope.launch {
            try {
                val result = if (nextState) {
                    wishlistRepository.addToWishlist(userId, eventId)
                } else {
                    wishlistRepository.removeFromWishlist(userId, eventId)
                }
                when (result) {
                    is UiState.Success -> onFeedback(result.data)
                    is UiState.Error -> {
                        syncFavoriteStateLocally(eventId, previousState)
                        onFeedback(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                syncFavoriteStateLocally(eventId, previousState)
                onFeedback("Gagal memperbarui favorit di server.")
            }
        }
    }

    fun submitEvent(
        existingEventId: String?,
        eventInput: EventModel,
        selectedImageBytes: ByteArray?,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _formState.update { UiState.Loading }
            try {
                var finalImageUrl = eventInput.imageUrl
                if (selectedImageBytes != null) {
                    val fileName = "poster_${System.currentTimeMillis()}.jpg"
                    when (val uploadResult = repository.uploadPoster(fileName, selectedImageBytes)) {
                        is UiState.Success -> finalImageUrl = uploadResult.data
                        is UiState.Error -> {
                            _formState.update { UiState.Error("Gagal mengunggah poster: ${uploadResult.message}") }
                            return@launch
                        }
                        else -> {}
                    }
                }

                val payload = eventInput.copy(imageUrl = finalImageUrl)
                val result = if (!existingEventId.isNullOrBlank()) {
                    repository.updateEvent(existingEventId, payload)
                } else {
                    repository.addEvent(payload)
                }

                when (result) {
                    is UiState.Success -> {
                        val saved = result.data.copy(isFavorite = eventInput.isFavorite)
                        rawEvents = if (!existingEventId.isNullOrBlank()) {
                            rawEvents.map { if (it.id == existingEventId) saved else it }
                        } else {
                            listOf(saved) + rawEvents
                        }
                        if (!existingEventId.isNullOrBlank()) {
                            _eventDetailState.update { UiState.Success(saved) }
                        }
                        updateAvailableLocations()
                        applyFilter()
                        _formState.update {
                            UiState.Success(
                                if (!existingEventId.isNullOrBlank()) "Event berhasil diperbarui" else "Event berhasil ditambahkan"
                            )
                        }
                        onComplete()
                    }
                    is UiState.Error -> {
                        _formState.update { UiState.Error(result.message) }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _formState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun deleteEvent(
        id: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                when (val result = repository.deleteEvent(id)) {
                    is UiState.Success -> {
                        rawEvents = rawEvents.filterNot { it.id == id }
                        updateAvailableLocations()
                        applyFilter()
                        onComplete()
                    }
                    is UiState.Error -> {
                        onError(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError(e.toUserFriendlyMessage())
            }
        }
    }

    private fun updateAvailableLocations() {
        val cities = rawEvents
            .mapNotNull { event ->
                val loc = event.location.trim()
                if (loc.isBlank()) null else extractCityOrVenue(loc)
            }
            .distinct()
            .sorted()
        _availableLocations.update { listOf("Semua Lokasi") + cities }
    }

    private fun extractCityOrVenue(location: String): String {
        val knownCities = listOf("Jakarta", "Bandung", "Surabaya", "Bali", "Tangerang", "Yogyakarta", "Medan", "Semarang")
        return knownCities.firstOrNull { location.contains(it, ignoreCase = true) } ?: location
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        val cat = _selectedCategory.value
        val loc = _selectedLocation.value
        val sort = _sortOption.value

        val filtered = rawEvents.filter { event ->
            val matchesCategory = (cat == "Semua" || event.category.equals(cat, ignoreCase = true))
            val matchesLocation = (loc == "Semua Lokasi" || event.location.contains(loc, ignoreCase = true))
            val matchesQuery = query.isEmpty() ||
                event.title.lowercase().contains(query) ||
                event.description.lowercase().contains(query) ||
                event.location.lowercase().contains(query)
            matchesCategory && matchesLocation && matchesQuery
        }

        val sorted = when (sort) {
            KatalogSortOption.TERBARU -> filtered
            KatalogSortOption.HARGA_TERENDAH -> filtered.sortedBy { it.price }
            KatalogSortOption.HARGA_TERTINGGI -> filtered.sortedByDescending { it.price }
        }

        _eventsState.update { UiState.Success(sorted) }
    }
}
