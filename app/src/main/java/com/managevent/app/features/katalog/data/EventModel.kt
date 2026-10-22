package com.managevent.app.features.katalog.data

import kotlinx.serialization.Serializable

@Serializable
data class EventModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val date: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null
)
