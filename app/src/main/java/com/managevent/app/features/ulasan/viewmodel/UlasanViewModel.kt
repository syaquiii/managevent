package com.managevent.app.features.ulasan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.katalog.data.KatalogRepository
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.data.UlasanRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RatingSummary(
    val averageRating: Double = 0.0,
    val totalCount: Int = 0,
    val countsByStar: Map<Int, Int> = (1..5).associateWith { 0 },
    val percentagesByStar: Map<Int, Float> = (1..5).associateWith { 0f }
)

class UlasanViewModel(
    private val repository: UlasanRepository = UlasanRepository(),
    private val katalogRepository: KatalogRepository = KatalogRepository()
) : ViewModel() {

    private val _ulasanState = MutableStateFlow<UiState<List<UlasanModel>>>(UiState.Idle)
    val ulasanState: StateFlow<UiState<List<UlasanModel>>> = _ulasanState.asStateFlow()

    private val _ratingSummary = MutableStateFlow(RatingSummary())
    val ratingSummary: StateFlow<RatingSummary> = _ratingSummary.asStateFlow()

    private val _eventTitle = MutableStateFlow("")
    val eventTitle: StateFlow<String> = _eventTitle.asStateFlow()

    private val _selectedRatingFilter = MutableStateFlow(0)
    val selectedRatingFilter: StateFlow<Int> = _selectedRatingFilter.asStateFlow()

    private val _ratingInput = MutableStateFlow(5)
    val ratingInput: StateFlow<Int> = _ratingInput.asStateFlow()

    private val _commentInput = MutableStateFlow("")
    val commentInput: StateFlow<String> = _commentInput.asStateFlow()

    private val _commentError = MutableStateFlow<String?>(null)
    val commentError: StateFlow<String?> = _commentError.asStateFlow()

    private val _editingUlasanId = MutableStateFlow<String?>(null)
    val editingUlasanId: StateFlow<String?> = _editingUlasanId.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _actionMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val actionMessage: SharedFlow<String> = _actionMessage.asSharedFlow()

    private var allUlasan: List<UlasanModel> = emptyList()

    fun fetchUlasan(eventId: String) {
        viewModelScope.launch {
            _ulasanState.update { UiState.Loading }
            try {
                when (val eventRes = katalogRepository.getEventById(eventId)) {
                    is UiState.Success -> _eventTitle.update { eventRes.data.title }
                    else -> {}
                }

                when (val result = repository.getUlasanByEvent(eventId)) {
                    is UiState.Success -> {
                        allUlasan = result.data
                        computeRatingSummary()
                        applyFilter()
                    }
                    is UiState.Error -> {
                        _ulasanState.update { UiState.Error(result.message) }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ulasanState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun onFilterChanged(rating: Int) {
        _selectedRatingFilter.update { rating }
        applyFilter()
    }

    private fun computeRatingSummary() {
        val total = allUlasan.size
        if (total == 0) {
            _ratingSummary.update { RatingSummary() }
            return
        }
        val avg = allUlasan.map { it.rating }.average()
        val counts = (1..5).associateWith { star -> allUlasan.count { it.rating == star } }
        val percentages = (1..5).associateWith { star -> (counts[star] ?: 0).toFloat() / total.toFloat() }
        _ratingSummary.update {
            RatingSummary(
                averageRating = avg,
                totalCount = total,
                countsByStar = counts,
                percentagesByStar = percentages
            )
        }
    }

    private fun applyFilter() {
        val filter = _selectedRatingFilter.value
        val filtered = if (filter == 0) allUlasan else allUlasan.filter { it.rating == filter }
        _ulasanState.update { UiState.Success(filtered) }
    }

    fun startEdit(ulasan: UlasanModel) {
        _editingUlasanId.update { ulasan.id }
        _ratingInput.update { ulasan.rating }
        _commentInput.update { ulasan.comment }
        _commentError.update { null }
    }

    fun resetForm() {
        _editingUlasanId.update { null }
        _ratingInput.update { 5 }
        _commentInput.update { "" }
        _commentError.update { null }
    }

    fun onRatingChange(newRating: Int) {
        _ratingInput.update { newRating.coerceIn(1, 5) }
    }

    fun onCommentChange(newComment: String) {
        _commentInput.update { newComment }
        _commentError.update { null }
    }

    fun submitUlasan(
        eventId: String,
        userId: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val comment = _commentInput.value.trim()
        if (comment.isBlank()) {
            _commentError.update { "Komentar ulasan wajib diisi" }
            return
        }

        val editId = _editingUlasanId.value
        val rating = _ratingInput.value

        viewModelScope.launch {
            _isSubmitting.update { true }
            try {
                val result = if (editId != null) {
                    repository.updateUlasan(editId, comment, rating)
                } else {
                    repository.addUlasan(eventId, userId, username, rating, comment)
                }
                when (result) {
                    is UiState.Success -> {
                        resetForm()
                        fetchUlasan(eventId)
                        onSuccess()
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
            } finally {
                _isSubmitting.update { false }
            }
        }
    }

    fun deleteUlasan(ulasanId: String, eventId: String) {
        viewModelScope.launch {
            try {
                when (val result = repository.deleteUlasan(ulasanId)) {
                    is UiState.Success -> {
                        _actionMessage.emit(result.data)
                        fetchUlasan(eventId)
                    }
                    is UiState.Error -> {
                        _actionMessage.emit(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionMessage.emit(e.toUserFriendlyMessage())
            }
        }
    }
}
