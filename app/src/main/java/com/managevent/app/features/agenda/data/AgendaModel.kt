package com.managevent.app.features.agenda.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgendaModel(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("event_id") val eventId: String? = null,
    val title: String,
    val date: String,                 // ISO: 2026-12-20
    val notes: String? = null,
    @SerialName("is_done") val isDone: Boolean = false
)