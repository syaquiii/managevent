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

    fun fetchUlasan(eventId: String) {
        viewModelScope.launch {
            _ulasanState.value = UiState.Loading
            when (val result = repository.getUlasanByEvent(eventId)) {
                is UiState.Success -> {
                    allUlasan = result.data
                    applyFilter()
                }
                is UiState.Error -> {
                    _ulasanState.value = UiState.Error(result.message)
                }
                else -> {}
            }
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

    fun submitUlasan(
        eventId: String,
        userId: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _ulasanState.value = UiState.Loading

            if (editingUlasanId != null) {
                when (val result = repository.updateUlasan(editingUlasanId!!, commentInput, ratingInput)) {
                    is UiState.Success -> {
                        resetForm()
                        fetchUlasan(eventId)
                        onSuccess()
                    }
                    is UiState.Error -> {
                        _ulasanState.value = UiState.Error(result.message)
                        onError(result.message)
                    }
                    else -> {}
                }
            } else {
                when (val result = repository.addUlasan(eventId, userId, username, ratingInput, commentInput)) {
                    is UiState.Success -> {
                        resetForm()
                        fetchUlasan(eventId)
                        onSuccess()
                    }
                    is UiState.Error -> {
                        _ulasanState.value = UiState.Error(result.message)
                        onError(result.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteUlasan(ulasanId: String, eventId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteUlasan(ulasanId)) {
                is UiState.Success -> fetchUlasan(eventId)
                is UiState.Error -> println("Gagal hapus: ${result.message}")
                else -> {}
            }
        }
    }
}