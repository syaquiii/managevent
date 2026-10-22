package com.managevent.app.features.agenda.data

import kotlinx.serialization.Serializable

@Serializable
data class AgendaModel(
    val id: String = "",
    val userId: String = "",
    val eventId: String? = null,
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val notes: String = ""
)
