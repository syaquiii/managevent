package com.managevent.app.features.agenda.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.features.pemesanan.data.PemesananRepository
import com.managevent.app.features.pemesanan.data.TransaksiModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class AgendaRepository {
    private val client = SupabaseClientProvider.client
    private val pemesananRepository = PemesananRepository()

    suspend fun getBookedEvents(): List<BookedEventModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid == null) {
            val demoOrders = when (val res = pemesananRepository.getRiwayatPemesanan()) {
                is UiState.Success -> res.data
                else -> emptyList()
            }
            return@withContext DemoAgendaStore.getBookedEvents(demoOrders)
        }

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

            if (result.isEmpty()) {
                val demoOrders = when (val res = pemesananRepository.getRiwayatPemesanan()) {
                    is UiState.Success -> res.data
                    else -> emptyList()
                }
                DemoAgendaStore.getBookedEvents(demoOrders)
            } else result
        } catch (_: Exception) {
            val demoOrders = when (val res = pemesananRepository.getRiwayatPemesanan()) {
                is UiState.Success -> res.data
                else -> emptyList()
            }
            DemoAgendaStore.getBookedEvents(demoOrders)
        }
    }

    suspend fun getNotes(eventId: String): List<AgendaModel> = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid == null) return@withContext DemoAgendaStore.getNotes(eventId)

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

            if (list.isEmpty()) DemoAgendaStore.getNotes(eventId) else list
        } catch (_: Exception) {
            DemoAgendaStore.getNotes(eventId)
        }
    }

    suspend fun addNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid == null) {
            DemoAgendaStore.addNote(note)
            return@withContext
        }
        try {
            client.postgrest["agenda"].insert(note.copy(id = null, userId = uid))
        } catch (_: Exception) {
            DemoAgendaStore.addNote(note)
        }
        Unit
    }

    suspend fun updateNote(note: AgendaModel) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        val id = note.id ?: return@withContext
        if (uid == null || isDummyId(id)) {
            DemoAgendaStore.updateNote(note)
            return@withContext
        }

        try {
            client.postgrest["agenda"].update({
                set("title", note.title)
                set("notes", note.notes)
                set("date", note.date)
                set("is_done", note.isDone)
            }) { filter { eq("id", id) } }
        } catch (_: Exception) {
            DemoAgendaStore.updateNote(note)
        }
        Unit
    }

    suspend fun setDone(id: String, done: Boolean) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid == null || isDummyId(id)) {
            DemoAgendaStore.setDone(id, done)
            return@withContext
        }

        try {
            client.postgrest["agenda"].update({ set("is_done", done) }) {
                filter { eq("id", id) }
            }
        } catch (_: Exception) {
            DemoAgendaStore.setDone(id, done)
        }
        Unit
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        val uid = client.auth.currentUserOrNull()?.id
        if (uid == null || isDummyId(id)) {
            DemoAgendaStore.deleteNote(id)
            return@withContext
        }

        try {
            client.postgrest["agenda"].delete { filter { eq("id", id) } }
        } catch (_: Exception) {
            DemoAgendaStore.deleteNote(id)
        }
        Unit
    }

    private fun isDummyId(id: String): Boolean {
        return !isValidUuid(id)
    }

    private fun isValidUuid(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (_: Exception) {
            false
        }
    }

    private object DemoAgendaStore {
        private val lock = Any()

        private val bookedEvents = mutableListOf(
            BookedEventModel(
                eventId = "e1",
                title = "Pameran Pernikahan Nusantara 2026",
                date = "18 Juli 2026",
                location = "Grand Ballroom Jakarta",
                imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                status = "Berjalan",
                noteCount = 4
            ),
            BookedEventModel(
                eventId = "e2",
                title = "Jakarta Jazz Symphony 2026",
                date = "24 Agustus 2026",
                location = "Istora Senayan Jakarta",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800",
                status = "Berjalan",
                noteCount = 2
            ),
            BookedEventModel(
                eventId = "e3",
                title = "Bandung Creative Summit 2026",
                date = "12 Maret 2026",
                location = "Convention Hall Bandung",
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?q=80&w=800",
                status = "Selesai",
                noteCount = 1
            )
        )

        private val notesMap = mutableMapOf<String, MutableList<AgendaModel>>(
            "e1" to mutableListOf(
                AgendaModel("11111111-1111-1111-1111-111111111111", null, "e1", "Konfirmasi busana pengantin", "2026-06-25", "Fitting busana akhir bersama Sanggar Nusantara", true),
                AgendaModel("22222222-2222-2222-2222-222222222222", null, "e1", "Cetak tiket & barcode QR", "2026-07-02", "Koordinasi vendor percetakan", false),
                AgendaModel("33333333-3333-3333-3333-333333333333", null, "e1", "Susun rundown kedatangan vendor", "2026-07-10", "Plotting waktu loading-in sound system", false),
                AgendaModel("44444444-4444-4444-4444-444444444444", null, "e1", "Review checklist katering", "2026-07-14", "Finalisasi 12 menu prasmanan", false)
            ),
            "e2" to mutableListOf(
                AgendaModel("55555555-5555-5555-5555-555555555555", null, "e2", "Pengambilan ID Pass VIP", "2026-08-20", "Ambil di booth panitia Istora", false),
                AgendaModel("66666666-6666-6666-6666-666666666666", null, "e2", "Konfirmasi transportasi tim", "2026-08-22", "Sewa van 12 seat", true)
            ),
            "e3" to mutableListOf(
                AgendaModel("77777777-7777-7777-7777-777777777777", null, "e3", "Evaluasi acara", "2026-03-13", "Laporan akhir panitia", true)
            )
        )

        fun getBookedEvents(demoOrders: List<TransaksiModel>): List<BookedEventModel> = synchronized(lock) {
            val list = bookedEvents.toMutableList()

            for (order in demoOrders) {
                if (list.none { it.eventId == order.eventId }) {
                    list.add(
                        0,
                        BookedEventModel(
                            eventId = order.eventId,
                            title = order.eventTitle.ifBlank { "Event Dipesan" },
                            date = order.eventDate.ifBlank { "2026-12-31" },
                            location = "Jakarta",
                            imageUrl = order.eventImageUrl,
                            status = order.status,
                            noteCount = notesMap[order.eventId]?.size ?: 0
                        )
                    )
                }
            }

            list.map { item ->
                val count = notesMap[item.eventId]?.size ?: 0
                item.copy(noteCount = count)
            }
        }

        fun getNotes(eventId: String): List<AgendaModel> = synchronized(lock) {
            notesMap[eventId]?.toList() ?: emptyList()
        }

        fun addNote(note: AgendaModel) {
            synchronized(lock) {
                val eventId = note.eventId ?: return
                val list = notesMap.getOrPut(eventId) { mutableListOf() }
                val newNote = note.copy(id = UUID.randomUUID().toString())
                list.add(newNote)
            }
        }

        fun updateNote(note: AgendaModel) {
            synchronized(lock) {
                val eventId = note.eventId ?: return
                val list = notesMap[eventId] ?: return
                val index = list.indexOfFirst { it.id == note.id || it.title == note.title }
                if (index != -1) {
                    list[index] = note
                }
            }
        }

        fun setDone(id: String, done: Boolean) = synchronized(lock) {
            for ((_, list) in notesMap) {
                val index = list.indexOfFirst { it.id == id }
                if (index != -1) {
                    list[index] = list[index].copy(isDone = done)
                    break
                }
            }
        }

        fun deleteNote(id: String) = synchronized(lock) {
            for ((_, list) in notesMap) {
                val removed = list.removeAll { it.id == id || it.title == id }
                if (removed) break
            }
        }
    }
}
