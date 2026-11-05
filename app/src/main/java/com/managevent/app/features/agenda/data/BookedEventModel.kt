    package com.managevent.app.features.agenda.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class BookedEventModel(
    val eventId: String,
    val title: String,
    val date: String?,
    val location: String?,
    val imageUrl: String?,
    val status: String,        // "Berjalan" / "Selesai"
    val noteCount: Int
)

@Serializable
internal data class BookedRowDto(
    @SerialName("event_id") val eventId: String? = null,
    val status: String? = null,
    val events: EventInfoDto? = null
)

@Serializable
internal data class EventInfoDto(
    val id: String,
    val title: String,
    val date: String? = null,
    val location: String? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
internal data class AgendaEventIdDto(
    @SerialName("event_id") val eventId: String? = null
)