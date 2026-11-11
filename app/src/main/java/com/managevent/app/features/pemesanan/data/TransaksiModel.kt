package com.managevent.app.features.pemesanan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TransaksiModel(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("event_id") val eventId: String = "",
    @SerialName("jumlah_tiket") val jumlahTiket: Int = 1,
    @SerialName("catatan") val catatan: String? = null,
    @SerialName("total_harga") val totalHarga: Double = 0.0,
    @SerialName("status") val status: String = "MENUNGGU_PEMBAYARAN",
    @SerialName("status_pembayaran") val statusPembayaran: String = "PENDING",
    @SerialName("metode_pembayaran") val metodePembayaran: String = "bca_va",
    @SerialName("nomor_virtual_account") val nomorVirtualAccount: String? = null,
    @SerialName("kode_referensi") val kodeReferensi: String? = null,
    @SerialName("payment_expires_at") val paymentExpiresAt: String? = null,
    @SerialName("paid_at") val paidAt: String? = null,
    @SerialName("created_at") val createdAt: String = "",


    @Transient val eventTitle: String = "",
    @Transient val eventDate: String = "",
    @Transient val eventImageUrl: String? = null
)