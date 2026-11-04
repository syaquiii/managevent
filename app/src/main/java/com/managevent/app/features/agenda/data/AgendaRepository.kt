package com.managevent.app.features.agenda.data

import com.managevent.app.core.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AgendaRepository {
    private val client = SupabaseClientProvider.client

    private val defaultBookedEvents = listOf(
        BookedEventModel(
            eventId = "11111111-1111-1111-1111-111111111111",
            title = "Pameran Pernikahan Nusantara 2026",
            date = "18 Juli 2026",
            location = "Grand Ballroom Jakarta",
            imageUrl = null,
            status = "Berjalan",
            noteCount = 6
        ),
        BookedEventModel(
            eventId = "22222222-2222-2222-2222-222222222222",
            title = "Jakarta Jazz Symphony 2026",
            date = "24 Agustus 2026",
            location = "Istora Senayan Jakarta",
            imageUrl = null,
            status = "Berjalan",
            noteCount = 4
        ),
        BookedEventModel(
            eventId = "33333333-3333-3333-3333-333333333333",
            title = "Bandung Creative Summit 2026",
            date = "12 Maret 2026",
            location = "Convention Hall Bandung",
            imageUrl = null,
            status = "Selesai",
            noteCount = 8
        )
    )

    private fun defaultNotes(eventId: String) = listOf(
        AgendaModel("11111111-1111-1111-1111-111111111111", null, eventId, "Konfirmasi busana pengantin", "2026-06-25", "Fitting busana akhir bersama Sanggar Nusantara", true),
        AgendaModel("22222222-2222-2222-2222-222222222222", null, eventId, "Cetak tiket & barcode QR", "2026-07-02", "Koordinasi vendor percetakan", false),
        AgendaModel("33333333-3333-3333-3333-333333333333", null, eventId, "Susun rundown kedatangan vendor", "2026-07-10", "Plotting waktu loading-in sound system", false),
        AgendaModel("44444444-4444-4444-4444-444444444444", null, eventId, "Review checklist katering", "2026-07-14", "Finalisasi 12 menu prasmanan", false)
    )

    private fun isDummyId(id: String): Boolean {
        return id.startsWith("11111111") || id.startsWith("22222222") ||
                id.startsWith("33333333") || id.startsWith("44444444") ||
                !isValidUuid(id)
    }

    private fun isValidUuid(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getBookedEvents(): List<BookedEventModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext defaultBookedEvents

        try {
            val rows = client.postgrest["transaksi"]
                .select(Columns.raw("event_id, status, events(id, title, date, location, image_url)")) {
                    filter { eq("user_id", uid) }
                }
                .decodeList<BookedRowDto>()

            val counts = client.postgrest["agenda"]
                .select(Columns.list("event_id")) { filter { eq("user_id", uid) } }
                .decodeList<AgendaEventIdDto>()
                .mapNotNull { it.eventId }
                .groupingBy { it }
                .eachCount()

            val result = rows.filter { it.events != null }
                .distinctBy { it.events!!.id }
                .map {
                    val e = it.events!!
                    BookedEventModel(
                        eventId = e.id,
                        title = e.title,
                        date = e.date,
                        location = e.location,
                        imageUrl = e.imageUrl,
                        status = it.status ?: "Berjalan",
                        noteCount = counts[e.id] ?: 0
                    )
                }

            if (result.isEmpty()) defaultBookedEvents else result
        } catch (_: Exception) {
            defaultBookedEvents
        }
    }

    suspend fun getNotes(eventId: String): List<AgendaModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext defaultNotes(eventId)

        try {
            val list = client.postgrest["agenda"]
                .select {
                    filter {
                        eq("user_id", uid)
                        eq("event_id", eventId)
                    }
                }
                .decodeList<AgendaModel>()
                .sortedBy { it.date }

            if (list.isEmpty()) defaultNotes(eventId) else list
        } catch (_: Exception) {
            defaultNotes(eventId)
        }
    }

    suspend fun addNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext
        try {
            client.postgrest["agenda"].insert(note.copy(id = null, userId = uid))
        } catch (_: Exception) {}
        Unit
    }

    suspend fun updateNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        if (client.auth.currentUserOrNull() == null) return@withContext
        val id = note.id ?: return@withContext
        if (isDummyId(id)) return@withContext

        try {
            client.postgrest["agenda"].update({
                set("title", note.title)
                set("notes", note.notes)
                set("date", note.date)
                set("is_done", note.isDone)
            }) { filter { eq("id", id) } }
        } catch (_: Exception) {}
        Unit
    }

    suspend fun setDone(id: String, done: Boolean) = withContext(Dispatchers.IO) {
        if (client.auth.currentUserOrNull() == null || isDummyId(id)) return@withContext

        try {
            client.postgrest["agenda"].update({ set("is_done", done) }) {
                filter { eq("id", id) }
            }
        } catch (_: Exception) {}
        Unit
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        if (client.auth.currentUserOrNull() == null || isDummyId(id)) return@withContext

        try {
            client.postgrest["agenda"].delete { filter { eq("id", id) } }
        } catch (_: Exception) {}
        Unit
    }
}
