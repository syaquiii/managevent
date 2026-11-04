package com.managevent.app.features.katalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.data.KatalogRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KatalogViewModel(
    private val repository: KatalogRepository = KatalogRepository()
) : ViewModel() {

    private var rawEvents: List<EventModel> = emptyList()

    private val _eventsState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val eventsState: StateFlow<UiState<List<EventModel>>> = _eventsState.asStateFlow()

    private val _eventDetailState = MutableStateFlow<UiState<EventModel>>(UiState.Idle)
    val eventDetailState: StateFlow<UiState<EventModel>> = _eventDetailState.asStateFlow()

    private val _formState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val formState: StateFlow<UiState<String>> = _formState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = UiState.Loading
            try {
                when (val result = repository.getEvents()) {
                    is UiState.Success -> {
                        rawEvents = result.data
                        applyFilter()
                    }
                    is UiState.Error -> {
                        _eventsState.value = UiState.Error(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventsState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun fetchEventById(id: String) {
        viewModelScope.launch {
            _eventDetailState.value = UiState.Loading
            try {
                val localFound = rawEvents.find { it.id == id }
                if (localFound != null) {
                    _eventDetailState.value = UiState.Success(localFound)
                } else {
                    _eventDetailState.value = repository.getEventById(id)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventDetailState.value = UiState.Error(e.localizedMessage ?: "Gagal memuat detail event")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }

    fun toggleFavorite(eventId: String) {
        rawEvents = rawEvents.map { event ->
            if (event.id == eventId) {
                event.copy(isFavorite = !event.isFavorite)
            } else {
                event
            }
        }
        applyFilter()
    }

    fun addEvent(event: EventModel, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            try {
                val newId = if (event.id.isBlank()) "evt-${System.currentTimeMillis()}" else event.id
                val updatedEvent = event.copy(id = newId)
                val result = repository.addEvent(updatedEvent)
                if (result is UiState.Success) {
                    rawEvents = rawEvents + updatedEvent
                    applyFilter()
                    onComplete()
                }
                _formState.value = result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _formState.value = UiState.Error(e.localizedMessage ?: "Gagal menambah event")
            }
        }
    }

    fun updateEvent(id: String, event: EventModel, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _formState.value = UiState.Loading
            try {
                val result = repository.updateEvent(id, event)
                if (result is UiState.Success) {
                    rawEvents = rawEvents.map { if (it.id == id) event else it }
                    applyFilter()
                    onComplete()
                }
                _formState.value = result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _formState.value = UiState.Error(e.localizedMessage ?: "Gagal memperbarui event")
            }
        }
    }

    fun deleteEvent(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _eventsState.value = UiState.Loading
            try {
                val result = repository.deleteEvent(id)
                if (result is UiState.Success) {
                    rawEvents = rawEvents.filterNot { it.id == id }
                    applyFilter()
                    onComplete()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventsState.value = UiState.Error(e.localizedMessage ?: "Gagal menghapus event")
            }
        }
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        val cat = _selectedCategory.value

        val filtered = rawEvents.filter { event ->
            val matchesCategory = (cat == "Semua" || event.category.equals(cat, ignoreCase = true))
            val matchesQuery = query.isEmpty() ||
                    event.title.lowercase().contains(query) ||
                    event.description.lowercase().contains(query) ||
                    event.location.lowercase().contains(query)
            matchesCategory && matchesQuery
        }

        _eventsState.value = UiState.Success(filtered)
    }
}
