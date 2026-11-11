package com.managevent.app.features.ulasan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.data.UlasanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UlasanViewModel(
    private val repository: UlasanRepository = UlasanRepository()
) : ViewModel() {

    private val _ulasanState = MutableStateFlow<UiState<List<UlasanModel>>>(UiState.Idle)
    val ulasanState: StateFlow<UiState<List<UlasanModel>>> = _ulasanState.asStateFlow()

    var selectedRatingFilter by mutableIntStateOf(0)
        private set

    var ratingInput by mutableIntStateOf(5)
        private set
    var commentInput by mutableStateOf("")
        private set

    var editingUlasanId by mutableStateOf<String?>(null)
        private set

    private var allUlasan: List<UlasanModel> = emptyList()
    private var isDummyLoaded = false // <-- Penanda biar data nggak ke-reset

    fun fetchUlasan(eventId: String) {
        viewModelScope.launch {
            if (isDummyLoaded) {
                applyFilter()
                return@launch
            }

            _ulasanState.value = UiState.Loading

            // --- DATA DUMMY AWAL ---
            kotlinx.coroutines.delay(500)
            allUlasan = listOf(
                UlasanModel(id = "1", userId = "dummy-user-id", username = "Kamu (Rayhan)", rating = 5, comment = "Acara sangat bagus! Saya sangat puas."),
                UlasanModel(id = "2", userId = "user-lain", username = "Sarah Wijaya", rating = 4, comment = "Secara keseluruhan oke, tapi antrean agak lama."),
                UlasanModel(id = "3", userId = "user-lain", username = "Budi Pratama", rating = 5, comment = "Visual panggung spektakuler!")
            )

            isDummyLoaded = true
            applyFilter()
        }
    }

    fun onFilterChanged(rating: Int) {
        selectedRatingFilter = rating
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (selectedRatingFilter == 0) allUlasan else allUlasan.filter { it.rating == selectedRatingFilter }
        _ulasanState.value = UiState.Success(filtered)
    }

    fun startEdit(ulasan: UlasanModel) {
        editingUlasanId = ulasan.id
        ratingInput = ulasan.rating
        commentInput = ulasan.comment
    }

    fun resetForm() {
        editingUlasanId = null
        ratingInput = 5
        commentInput = ""
    }

    fun onRatingChange(newRating: Int) { ratingInput = newRating }
    fun onCommentChange(newComment: String) { commentInput = newComment }

    fun submitUlasan(eventId: String, userId: String, username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Simulasi loading bentar

            if (editingUlasanId != null) {
                allUlasan = allUlasan.map {
                    if (it.id == editingUlasanId) it.copy(rating = ratingInput, comment = commentInput) else it
                }
            } else {
                val newUlasan = UlasanModel(
                    id = java.util.UUID.randomUUID().toString(),
                    eventId = eventId,
                    userId = userId,
                    username = username,
                    rating = ratingInput,
                    comment = commentInput
                )
                allUlasan = listOf(newUlasan) + allUlasan
            }

            applyFilter()
            resetForm()
            onSuccess()
        }
    }

    fun deleteUlasan(ulasanId: String, eventId: String) {
        viewModelScope.launch {
            allUlasan = allUlasan.filterNot { it.id == ulasanId }
            applyFilter()
        }
    }
}