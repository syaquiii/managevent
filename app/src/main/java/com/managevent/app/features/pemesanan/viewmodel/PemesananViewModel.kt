package com.managevent.app.features.pemesanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PemesananViewModel(
    private val repository: PemesananRepository = PemesananRepository(),
    private val paymentStepDelayMs: Long = 1000L
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
            _checkoutState.update { UiState.Idle }
        }
    }

    fun consumePaymentState() {
        _paymentState.update { UiState.Idle }
    }

    fun clearSessionData() {
        _riwayatState.update { UiState.Idle }
        _checkoutState.update { UiState.Idle }
        _detailState.update { UiState.Idle }
        _paymentState.update { UiState.Idle }
    }

    fun fetchRiwayat() {
        viewModelScope.launch {
            _riwayatState.update { UiState.Loading }
            try {
                _riwayatState.update { repository.getRiwayatPemesanan() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _riwayatState.update { UiState.Error(e.toUserFriendlyMessage()) }
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
            _checkoutState.update { UiState.Loading }
            try {
                val qty = jumlahTiket.coerceIn(1, 10)
                val transaksi = TransaksiModel(
                    eventId = eventId,
                    jumlahTiket = qty,
                    catatan = catatan.trim().ifBlank { null },
                    totalHarga = pricePerTicket * qty,
                    status = "MENUNGGU_PEMBAYARAN",
                    statusPembayaran = "PENDING",
                    eventTitle = eventTitle,
                    eventDate = eventDate,
                    eventImageUrl = eventImageUrl
                )
                _checkoutState.update { repository.checkout(transaksi) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _checkoutState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun payNow(transaksiId: String, bankCode: String) {
        viewModelScope.launch {
            _paymentState.update { UiState.Loading }
            try {
                if (paymentStepDelayMs > 0) {
                    _paymentLoadingMessage.update { "Menghubungkan ke sistem pembayaran..." }
                    delay(paymentStepDelayMs)
                    _paymentLoadingMessage.update { "Memverifikasi informasi pembayaran..." }
                    delay(paymentStepDelayMs)
                    _paymentLoadingMessage.update { "Memproses pembayaran..." }
                    delay(paymentStepDelayMs)
                }

                val result = repository.processPayment(transaksiId, bankCode)
                _paymentState.update { result }
                if (result is UiState.Success) {
                    _detailState.update { UiState.Success(result.data) }
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _paymentState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun renewPayment(transaksiId: String, bankCode: String) {
        viewModelScope.launch {
            _paymentState.update { UiState.Loading }
            try {
                val result = repository.renewPaymentInstruction(transaksiId, bankCode)
                _paymentState.update { result }
                if (result is UiState.Success) {
                    _detailState.update { UiState.Success(result.data) }
                    fetchRiwayat()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _paymentState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun fetchDetailById(transaksiId: String) {
        viewModelScope.launch {
            _detailState.update { UiState.Loading }
            try {
                val result = repository.getPemesananById(transaksiId)
                _detailState.update { result }
                if (result is UiState.Success) {
                    _paymentState.update { UiState.Idle }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun updateStatus(
        transaksiId: String,
        status: String,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                when (val result = repository.updateStatusPemesanan(transaksiId, status)) {
                    is UiState.Success -> {
                        val currentDetail = (_detailState.value as? UiState.Success)?.data
                        if (currentDetail != null) {
                            _detailState.update { UiState.Success(currentDetail.copy(status = status)) }
                        }
                        fetchRiwayat()
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

    fun cancelPemesanan(
        transaksiId: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                when (val result = repository.batalkanPemesanan(transaksiId)) {
                    is UiState.Success -> {
                        val currentDetail = (_detailState.value as? UiState.Success)?.data
                        if (currentDetail != null) {
                            _detailState.update { UiState.Success(currentDetail.copy(status = "DIBATALKAN", statusPembayaran = "DIBATALKAN")) }
                        }
                        fetchRiwayat()
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

    fun cancelWithReason(
        transaksiId: String,
        reason: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                when (val result = repository.batalkanPemesananWithReason(transaksiId, reason)) {
                    is UiState.Success -> {
                        val currentDetail = (_detailState.value as? UiState.Success)?.data
                        if (currentDetail != null) {
                            _detailState.update {
                                UiState.Success(
                                    currentDetail.copy(
                                        status = "DIBATALKAN",
                                        statusPembayaran = "DIBATALKAN",
                                        catatan = "Dibatalkan/Refund - Alasan: $reason"
                                    )
                                )
                            }
                        }
                        fetchRiwayat()
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
}
