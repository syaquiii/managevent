package com.managevent.app.features.katalog.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KatalogRepository {
    private val client = SupabaseClientProvider.client

    @Serializable
    private data class NewEventDto(
        val id: String? = null,
        val title: String,
        val description: String,
        val category: String,
        val date: String,
        val location: String,
        val price: Double,
        @SerialName("image_url") val imageUrl: String?
    )

    private fun EventModel.toNewDto() = NewEventDto(
        id = if (id.isNotBlank()) id else null,
        title = title,
        description = description,
        category = category,
        date = date,
        location = location,
        price = price,
        imageUrl = imageUrl
    )

    private suspend fun seedSupabaseEventsIfEmpty() {
        try {
            val existing = client.postgrest["events"].select().decodeList<EventModel>()
            if (existing.isEmpty()) {
                val seedData = listOf(
                    NewEventDto(
                        id = "11111111-1111-1111-1111-111111111111",
                        title = "Pameran Pernikahan Nusantara 2026",
                        description = "Pameran vendor pernikahan terbesar di Nusantara dengan promo eksklusif gaun, catering, dekorasi, dan dokumentasi terlengkap.",
                        category = "Pernikahan",
                        date = "18 Juli 2026",
                        location = "Jakarta Convention Center",
                        price = 150000.0,
                        imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "22222222-2222-2222-2222-222222222222",
                        title = "Jakarta Jazz Symphony 2026",
                        description = "Konser kolaborasi musisi jazz ternama tanah air dan orkestra internasional dengan penampilan panggung megah.",
                        category = "Konser",
                        date = "22 Agustus 2026",
                        location = "Istora Senayan, Jakarta",
                        price = 350000.0,
                        imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "33333333-3333-3333-3333-333333333333",
                        title = "Indonesia Tech Summit 2027",
                        description = "Konferensi teknologi & AI terbesar tahun 2027 menghadirkan pakar industri global, pameran startup, dan sesi networking.",
                        category = "Seminar",
                        date = "14 Februari 2027",
                        location = "ICE BSD Tangerang",
                        price = 250000.0,
                        imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "44444444-4444-4444-4444-444444444444",
                        title = "Kids & Family Birthday Festival",
                        description = "Festival ulang tahun anak dan keluarga terbesar dengan wahana permainan, panggung pertunjukan boneka, serta workshop kreasi.",
                        category = "Ulang Tahun",
                        date = "10 Mei 2026",
                        location = "Grand City Surabaya",
                        price = 75000.0,
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "55555555-5555-5555-5555-555555555555",
                        title = "Nusantara Symphony Fest",
                        description = "Konser musik simfoni megah menyajikan karya maestro Nusantara dengan orkestra lengkap.",
                        category = "Konser",
                        date = "14 Mei 2026",
                        location = "Gelora Bung Karno, Jakarta",
                        price = 250000.0,
                        imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "66666666-6666-6666-6666-666666666666",
                        title = "Gelaran Nikah Adat Nusantara",
                        description = "Pameran gaun dan adat pernikahan tradisional terlengkap dari Sabang sampai Merauke.",
                        category = "Pernikahan",
                        date = "22 Agustus 2026",
                        location = "JCC Senayan, Jakarta",
                        price = 75000.0,
                        imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "77777777-7777-7777-7777-777777777777",
                        title = "Bandung Rasa & Kopi Fest",
                        description = "Festival kuliner dan kopi terbesar se-Jawa Barat menghadirkan lebih dari 100 tenant kuliner terfavorit.",
                        category = "Festival",
                        date = "10 Oktober 2026",
                        location = "Jalan Braga, Bandung",
                        price = 50000.0,
                        imageUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=800"
                    ),
                    NewEventDto(
                        id = "88888888-8888-8888-8888-888888888888",
                        title = "Bali Immersive Art Expo",
                        description = "Pameran seni digital imersif di Ubud Bali menampilkan kolaborasi seniman lokal dan internasional.",
                        category = "Pameran",
                        date = "15 Januari 2027",
                        location = "Ubud Art Center, Bali",
                        price = 180000.0,
                        imageUrl = "https://images.unsplash.com/photo-1508997449629-303059a039c0?q=80&w=800"
                    )
                )
                client.postgrest["events"].insert(seedData)
            }
        } catch (_: Exception) {
            // Ignore if seeding fails
        }
    }

    suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                seedSupabaseEventsIfEmpty()
                val events = client.postgrest["events"].select().decodeList<EventModel>()
                UiState.Success(events)
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
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
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
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
                if (event.id.isBlank()) {
                    client.postgrest["events"].insert(event.toNewDto())
                } else {
                    client.postgrest["events"].insert(event)
                }
                UiState.Success("Event berhasil ditambahkan")
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun updateEvent(id: String, event: EventModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["events"].update(event.toNewDto()) {
                    filter { eq("id", id) }
                }
                UiState.Success("Event berhasil diperbarui")
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
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
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
}
