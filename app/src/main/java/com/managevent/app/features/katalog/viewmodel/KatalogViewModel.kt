package com.managevent.app.features.katalog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.data.KatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KatalogViewModel(
    private val repository: KatalogRepository = KatalogRepository()
) : ViewModel() {

    private val _eventsState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val eventsState: StateFlow<UiState<List<EventModel>>> = _eventsState.asStateFlow()

    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = UiState.Loading
            _eventsState.value = repository.getEvents()
        }
    }
}
