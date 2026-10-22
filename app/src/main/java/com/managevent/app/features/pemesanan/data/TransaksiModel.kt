package com.managevent.app.features.pemesanan.data

import kotlinx.serialization.Serializable

@Serializable
data class TransaksiModel(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val jumlahTiket: Int = 1,
    val totalHarga: Double = 0.0,
    val status: String = "PENDING",
    val createdAt: String = ""
)
