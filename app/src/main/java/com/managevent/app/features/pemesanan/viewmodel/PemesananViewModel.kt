package com.managevent.app.features.pemesanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PemesananViewModel(
    private val repository: PemesananRepository = PemesananRepository()
) : ViewModel() {

    private val _riwayatState = MutableStateFlow<UiState<List<TransaksiModel>>>(UiState.Idle)
    val riwayatState: StateFlow<UiState<List<TransaksiModel>>> = _riwayatState.asStateFlow()

    fun fetchRiwayat(userId: String) {
        viewModelScope.launch {
            _riwayatState.value = UiState.Loading
            _riwayatState.value = repository.getRiwayatPemesanan(userId)
        }
    }
}
