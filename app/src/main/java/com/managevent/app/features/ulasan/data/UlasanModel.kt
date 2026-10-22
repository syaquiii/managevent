package com.managevent.app.features.ulasan.data

import kotlinx.serialization.Serializable

@Serializable
data class UlasanModel(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val username: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val createdAt: String = ""
)
