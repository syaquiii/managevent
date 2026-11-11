package com.managevent.app.features.pemesanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PemesananViewModel(
    private val repository: PemesananRepository = PemesananRepository()
) : ViewModel() {

    private val _riwayatState = MutableStateFlow<UiState<List<TransaksiModel>>>(UiState.Idle)
    val riwayatState: StateFlow<UiState<List<TransaksiModel>>> = _riwayatState.asStateFlow()

    private val _checkoutState = MutableStateFlow<UiState<TransaksiModel>>(UiState.Idle)
    val checkoutState: StateFlow<UiState<TransaksiModel>> = _checkoutState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<TransaksiModel>>(UiState.Idle)
    val detailState: StateFlow<UiState<TransaksiModel>> = _detailState.asStateFlow()

    private val _paymentState = MutableStateFlow<UiState<TransaksiModel>>(UiState.Idle)
    val paymentState: StateFlow<UiState<TransaksiModel>> = _paymentState.asStateFlow()

    private val _paymentLoadingMessage = MutableStateFlow("Menghubungkan ke sistem pembayaran...")
    val paymentLoadingMessage: StateFlow<String> = _paymentLoadingMessage.asStateFlow()

    fun consumeCheckoutSuccess() {
        if (_checkoutState.value is UiState.Success) {
            _checkoutState.value = UiState.Idle
        }
    }

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
                    status = "MENUNGGU_PEMBAYARAN",
                    statusPembayaran = "PENDING",
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

    fun payNow(transaksiId: String, bankCode: String) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            try {
                _paymentLoadingMessage.value = "Menghubungkan ke sistem pembayaran..."
                delay(1000)
                _paymentLoadingMessage.value = "Memverifikasi informasi pembayaran..."
                delay(1200)
                _paymentLoadingMessage.value = "Memproses pembayaran..."
                delay(1000)

                val result = repository.processPayment(transaksiId, bankCode)
                _paymentState.value = result
                if (result is UiState.Success) {
                    _detailState.value = UiState.Success(result.data)
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _paymentState.value = UiState.Error(e.localizedMessage ?: "Gagal memproses pembayaran")
            }
        }
    }

    fun renewPayment(transaksiId: String, bankCode: String) {
        viewModelScope.launch {
            _paymentState.value = UiState.Loading
            try {
                val result = repository.renewPaymentInstruction(transaksiId, bankCode)
                _paymentState.value = result
                if (result is UiState.Success) {
                    _detailState.value = UiState.Success(result.data)
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _paymentState.value = UiState.Error(e.localizedMessage ?: "Gagal memperbarui instruksi pembayaran")
            }
        }
    }

    fun fetchDetailById(transaksiId: String) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            try {
                val result = repository.getPemesananById(transaksiId)
                _detailState.value = result
                if (result is UiState.Success) {
                    _paymentState.value = UiState.Idle
                }
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
                repository.updateStatusPemesanan(transaksiId, status)
                val currentDetail = (_detailState.value as? UiState.Success)?.data
                if (currentDetail != null) {
                    _detailState.value = UiState.Success(currentDetail.copy(status = status))
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.value = UiState.Error(e.localizedMessage ?: "Gagal memperbarui status")
            }
        }
    }

    fun cancelPemesanan(transaksiId: String) {
        viewModelScope.launch {
            try {
                repository.batalkanPemesanan(transaksiId)
                val currentDetail = (_detailState.value as? UiState.Success)?.data
                if (currentDetail != null) {
                    _detailState.value = UiState.Success(currentDetail.copy(status = "DIBATALKAN"))
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.value = UiState.Error(e.localizedMessage ?: "Gagal membatalkan pesanan")
            }
        }
    }
}