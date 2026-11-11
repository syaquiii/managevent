package com.managevent.app.features.agenda.data

import com.managevent.app.core.network.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgendaRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getBookedEvents(): List<BookedEventModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext emptyList()

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

            rows.filter { it.events != null }
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
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getNotes(eventId: String): List<AgendaModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext emptyList()

        try {
            client.postgrest["agenda"]
                .select {
                    filter {
                        eq("user_id", uid)
                        eq("event_id", eventId)
                    }
                }
                .decodeList<AgendaModel>()
                .sortedBy { it.date }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext
        try {
            client.postgrest["agenda"].insert(note.copy(id = null, userId = uid))
        } catch (_: Exception) {
        }
        Unit
    }

    suspend fun updateNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext
        val id = note.id ?: return@withContext

        try {
            client.postgrest["agenda"].update({
                set("title", note.title)
                set("notes", note.notes)
                set("date", note.date)
                set("is_done", note.isDone)
            }) { filter { eq("id", id) } }
        } catch (_: Exception) {
        }
        Unit
    }

    suspend fun setDone(id: String, done: Boolean) = withContext(Dispatchers.IO) {
        client.auth.currentUserOrNull()?.id ?: return@withContext

        try {
            client.postgrest["agenda"].update({ set("is_done", done) }) {
                filter { eq("id", id) }
            }
        } catch (_: Exception) {
        }
        Unit
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        client.auth.currentUserOrNull()?.id ?: return@withContext

        try {
            client.postgrest["agenda"].delete { filter { eq("id", id) } }
        } catch (_: Exception) {
        }
        Unit
    }
}
