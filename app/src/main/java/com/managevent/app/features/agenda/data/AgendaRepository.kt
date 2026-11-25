package com.managevent.app.features.agenda.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class AgendaRepository(
    private val clientProvider: () -> SupabaseClient = { SupabaseClientProvider.client }
) {
    private val client: SupabaseClient by lazy(clientProvider)

    open suspend fun getBookedEvents(): UiState<List<BookedEventModel>> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext UiState.Success(emptyList())

        try {
            val rows = client.postgrest["transaksi"]
                .select(Columns.raw("event_id, status, status_pembayaran, events(id, title, date, location, image_url)")) {
                    filter { eq("user_id", uid) }
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<BookedRowDto>()

            val validRows = rows.filter { row ->
                val isCancelled = row.status.equals("DIBATALKAN", ignoreCase = true) ||
                    row.statusPembayaran.equals("DIBATALKAN", ignoreCase = true)
                val isUnpaid = row.status.equals("MENUNGGU_PEMBAYARAN", ignoreCase = true) ||
                    row.statusPembayaran.equals("PENDING", ignoreCase = true) ||
                    row.statusPembayaran.equals("KEDALUWARSA", ignoreCase = true)
                row.events != null && !isCancelled && !isUnpaid
            }

            val counts = client.postgrest["agenda"]
                .select(Columns.list("event_id")) { filter { eq("user_id", uid) } }
                .decodeList<AgendaEventIdDto>()
                .mapNotNull { it.eventId }
                .groupingBy { it }
                .eachCount()

            val mapped = validRows
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
            UiState.Success(mapped)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    open suspend fun getNotes(eventId: String): UiState<List<AgendaModel>> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

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
            UiState.Success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    open suspend fun addNote(note: AgendaModel): UiState<String> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
        try {
            client.postgrest["agenda"].insert(note.copy(id = null, userId = uid))
            UiState.Success("Agenda ditambahkan")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    open suspend fun updateNote(note: AgendaModel): UiState<String> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
        val id = note.id ?: return@withContext UiState.Error("ID agenda tidak valid.")

        try {
            client.postgrest["agenda"].update({
                set("title", note.title)
                set("notes", note.notes)
                set("date", note.date)
                set("is_done", note.isDone)
            }) {
                filter {
                    eq("id", id)
                    eq("user_id", uid)
                }
            }
            UiState.Success("Agenda diperbarui")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    open suspend fun setDone(id: String, done: Boolean): UiState<String> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            client.postgrest["agenda"].update({ set("is_done", done) }) {
                filter {
                    eq("id", id)
                    eq("user_id", uid)
                }
            }
            UiState.Success(if (done) "Agenda selesai" else "Agenda dibuka kembali")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    open suspend fun deleteNote(id: String): UiState<String> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            client.postgrest["agenda"].delete {
                filter {
                    eq("id", id)
                    eq("user_id", uid)
                }
            }
            UiState.Success("Agenda dihapus")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }
}
