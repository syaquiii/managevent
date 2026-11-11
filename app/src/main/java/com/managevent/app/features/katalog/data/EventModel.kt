package com.managevent.app.features.katalog.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class EventModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val date: String = "",
    val location: String = "",
    val price: Double = 0.0,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @Transient
    val isFavorite: Boolean = false
)
