package com.managevent.app.features.ulasan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UlasanModel(
    val id: String = "",
    @SerialName("event_id") val eventId: String = "",
    @SerialName("user_id") val userId: String? = null, // Pakai ? agar tidak error jika null
    val username: String = "",
    val rating: Int = 5,
    val comment: String = "",
    @SerialName("created_at") val createdAt: String = ""
)