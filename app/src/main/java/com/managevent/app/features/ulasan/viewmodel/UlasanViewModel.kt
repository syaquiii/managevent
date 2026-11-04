package com.managevent.app.features.ulasan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.data.UlasanRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UlasanViewModel(
    private val repository: UlasanRepository = UlasanRepository()
) : ViewModel() {

    private val _ulasanState = MutableStateFlow<UiState<List<UlasanModel>>>(UiState.Idle)
    val ulasanState: StateFlow<UiState<List<UlasanModel>>> = _ulasanState.asStateFlow()

    fun fetchUlasan(eventId: String) {
        viewModelScope.launch {
            _ulasanState.value = UiState.Loading
            try {
                _ulasanState.value = repository.getUlasanByEvent(eventId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ulasanState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }
}
