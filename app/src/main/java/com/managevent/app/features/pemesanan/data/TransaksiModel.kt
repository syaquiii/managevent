package com.managevent.app.features.pemesanan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TransaksiModel(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("event_id")
    val eventId: String = "",
    @SerialName("jumlah_tiket")
    val jumlahTiket: Int = 1,
    @SerialName("catatan")
    val catatan: String? = null,
    @SerialName("total_harga")
    val totalHarga: Double = 0.0,
    @SerialName("status")
    val status: String = "Berjalan",
    @SerialName("created_at")
    val createdAt: String = "",
    @Transient
    val eventTitle: String = "",
    @Transient
    val eventDate: String = "",
    @Transient
    val eventImageUrl: String? = null
)
