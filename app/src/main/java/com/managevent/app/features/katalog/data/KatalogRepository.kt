package com.managevent.app.features.katalog.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KatalogRepository {
    private val client = SupabaseClientProvider.client

    fun getMockEvents(): List<EventModel> {
        return listOf(
            EventModel(
                id = "11111111-1111-1111-1111-111111111111",
                title = "Pameran Pernikahan Nusantara 2026",
                description = "Pameran vendor pernikahan terbesar di Nusantara dengan promo eksklusif gaun, catering, dekorasi, dan dokumentasi terlengkap.",
                category = "Pernikahan",
                date = "18 Juli 2026",
                location = "Jakarta Convention Center",
                price = 150000.0,
                imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                isFavorite = false
            ),
            EventModel(
                id = "22222222-2222-2222-2222-222222222222",
                title = "Jakarta Jazz Symphony 2026",
                description = "Konser kolaborasi musisi jazz ternama tanah air dan orkestra internasional dengan penampilan panggung megah.",
                category = "Konser",
                date = "22 Agustus 2026",
                location = "Istora Senayan, Jakarta",
                price = 350000.0,
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800",
                isFavorite = false
            ),
            EventModel(
                id = "33333333-3333-3333-3333-333333333333",
                title = "Indonesia Tech Summit 2027",
                description = "Konferensi teknologi & AI terbesar tahun 2027 menghadirkan pakar industri global, pameran startup, dan sesi networking.",
                category = "Seminar",
                date = "14 Februari 2027",
                location = "ICE BSD Tangerang",
                price = 250000.0,
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?q=80&w=800",
                isFavorite = false
            ),
            EventModel(
                id = "44444444-4444-4444-4444-444444444444",
                title = "Kids & Family Birthday Festival",
                description = "Festival ulang tahun anak dan keluarga terbesar dengan wahana permainan, panggung pertunjukan boneka, serta workshop kreasi.",
                category = "Ulang Tahun",
                date = "10 Mei 2026",
                location = "Grand City Surabaya",
                price = 75000.0,
                imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?q=80&w=800",
                isFavorite = false
            )
        )
    }

    suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = client.postgrest["events"].select().decodeList<EventModel>()
                if (events.isEmpty()) {
                    UiState.Success(getMockEvents())
                } else {
                    UiState.Success(events)
                }
            } catch (_: Exception) {
                UiState.Success(getMockEvents())
            }
        }
    }

    suspend fun getEventById(id: String): UiState<EventModel> {
        return withContext(Dispatchers.IO) {
            try {
                val event = client.postgrest["events"].select {
                    filter { eq("id", id) }
                }.decodeSingle<EventModel>()
                UiState.Success(event)
            } catch (_: Exception) {
                val mockEvent = getMockEvents().find { it.id == id }
                    ?: getMockEvents().first()
                UiState.Success(mockEvent)
            }
        }
    }

    suspend fun uploadPoster(fileName: String, byteArray: ByteArray): String? {
        return withContext(Dispatchers.IO) {
            try {
                val bucket = client.storage["posters"]
                bucket.upload(path = fileName, data = byteArray) {
                    upsert = true
                }
                bucket.publicUrl(fileName)
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun addEvent(event: EventModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["events"].insert(event)
                UiState.Success("Event berhasil ditambahkan")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menambahkan event")
            }
        }
    }

    suspend fun updateEvent(id: String, event: EventModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["events"].update(event) {
                    filter { eq("id", id) }
                }
                UiState.Success("Event berhasil diperbarui")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal memperbarui event")
            }
        }
    }

    suspend fun deleteEvent(id: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["events"].delete {
                    filter { eq("id", id) }
                }
                UiState.Success("Event berhasil dihapus")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menghapus event")
            }
        }
    }
}
