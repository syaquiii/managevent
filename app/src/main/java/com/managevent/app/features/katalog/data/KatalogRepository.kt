package com.managevent.app.features.katalog.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.auth.auth
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
        @SerialName("vendor_id") val vendorId: String,
        val title: String,
        val description: String,
        val category: String,
        val date: String,
        val location: String,
        val price: Double,
        @SerialName("image_url") val imageUrl: String?
    )

    private fun EventModel.toNewDto(vendorId: String) = NewEventDto(
        id = if (id.isNotBlank()) id else null,
        vendorId = vendorId,
        title = title,
        description = description,
        category = category,
        date = date,
        location = location,
        price = price,
        imageUrl = imageUrl
    )

    suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
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
            } catch (_: Exception) {
                UiState.Error("Event tidak ditemukan.")
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
                val currentUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Hanya vendor yang masuk yang dapat menambah event.")
                
                val dto = event.toNewDto(vendorId = currentUid)
                client.postgrest["events"].insert(dto)
                UiState.Success("Event berhasil ditambahkan")
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun updateEvent(id: String, event: EventModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Hanya vendor yang masuk yang dapat mengubah event.")
                
                val dto = event.toNewDto(vendorId = currentUid)
                client.postgrest["events"].update(dto) {
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
