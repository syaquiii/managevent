package com.managevent.app.features.pemesanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PemesananViewModel(
    private val repository: PemesananRepository = PemesananRepository()
) : ViewModel() {

    private val _riwayatState = MutableStateFlow<UiState<List<TransaksiModel>>>(UiState.Idle)
    val riwayatState: StateFlow<UiState<List<TransaksiModel>>> = _riwayatState.asStateFlow()

    private val _checkoutState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val checkoutState: StateFlow<UiState<String>> = _checkoutState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<TransaksiModel>>(UiState.Idle)
    val detailState: StateFlow<UiState<TransaksiModel>> = _detailState.asStateFlow()

    fun fetchRiwayat() {
        viewModelScope.launch {
            _riwayatState.value = UiState.Loading
            try {
                _riwayatState.value = repository.getRiwayatPemesanan()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _riwayatState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun submitCheckout(
        eventId: String,
        jumlahTiket: Int,
        catatan: String,
        pricePerTicket: Double,
        eventTitle: String = "",
        eventDate: String = "",
        eventImageUrl: String? = null
    ) {
        viewModelScope.launch {
            _checkoutState.value = UiState.Loading
            try {
                val transaksi = TransaksiModel(
                    eventId = eventId,
                    jumlahTiket = jumlahTiket.coerceIn(1, 10),
                    catatan = catatan.trim().ifBlank { null },
                    totalHarga = pricePerTicket * jumlahTiket.coerceIn(1, 10),
                    status = "Berjalan",
                    eventTitle = eventTitle,
                    eventDate = eventDate,
                    eventImageUrl = eventImageUrl
                )
                _checkoutState.value = repository.checkout(transaksi)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _checkoutState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan saat checkout")
            }
        }
    }

    fun fetchDetailById(transaksiId: String) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            try {
                _detailState.value = repository.getPemesananById(transaksiId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }

    fun updateStatus(transaksiId: String, status: String) {
        viewModelScope.launch {
            try {
                val updateResult = repository.updateStatusPemesanan(transaksiId, status)
                val currentDetail = (_detailState.value as? UiState.Success)?.data
                if (currentDetail != null) {
                    _detailState.value = UiState.Success(currentDetail.copy(status = status))
                    fetchRiwayat()
                }
                if (updateResult is UiState.Success) {
                    _checkoutState.value = UiState.Success(updateResult.data)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _checkoutState.value = UiState.Error(e.localizedMessage ?: "Gagal memperbarui status")
            }
        }
    }

    fun cancelPemesanan(transaksiId: String) {
        viewModelScope.launch {
            try {
                val cancelResult = repository.batalkanPemesanan(transaksiId)
                val currentDetail = (_detailState.value as? UiState.Success)?.data
                if (currentDetail != null) {
                    _detailState.value = UiState.Success(currentDetail.copy(status = "Dibatalkan"))
                    fetchRiwayat()
                }
                if (cancelResult is UiState.Success) {
                    _checkoutState.value = UiState.Success(cancelResult.data)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _checkoutState.value = UiState.Error(e.localizedMessage ?: "Gagal membatalkan pesanan")
            }
        }
    }
}
