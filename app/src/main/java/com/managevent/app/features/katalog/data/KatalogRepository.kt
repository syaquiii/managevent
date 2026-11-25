package com.managevent.app.features.katalog.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

open class KatalogRepository(
    private val clientProvider: () -> SupabaseClient = { SupabaseClientProvider.client }
) {
    private val client: SupabaseClient by lazy(clientProvider)

    @Serializable
    private data class NewEventDto(
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
        vendorId = vendorId,
        title = title.trim(),
        description = description.trim(),
        category = category.trim(),
        date = date.trim(),
        location = location.trim(),
        price = price,
        imageUrl = imageUrl?.takeIf { it.isNotBlank() }
    )

    open suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = client.postgrest["events"]
                    .select {
                        order("created_at", order = Order.DESCENDING)
                    }
                    .decodeList<EventModel>()
                UiState.Success(events)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun getEventById(id: String): UiState<EventModel> {
        return withContext(Dispatchers.IO) {
            try {
                val event = client.postgrest["events"].select {
                    filter { eq("id", id) }
                }.decodeSingle<EventModel>()
                UiState.Success(event)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                UiState.Error("Event tidak ditemukan.")
            }
        }
    }

    open suspend fun uploadPoster(fileName: String, byteArray: ByteArray): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val bucket = client.storage["posters"]
                bucket.upload(path = fileName, data = byteArray) {
                    upsert = true
                }
                UiState.Success(bucket.publicUrl(fileName))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun addEvent(event: EventModel): UiState<EventModel> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Hanya vendor yang masuk yang dapat menambah event.")

                val dto = event.toNewDto(vendorId = currentUid)
                val created = client.postgrest["events"]
                    .insert(dto) {
                        select()
                    }
                    .decodeSingle<EventModel>()
                UiState.Success(created)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun updateEvent(id: String, event: EventModel): UiState<EventModel> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Hanya vendor yang masuk yang dapat mengubah event.")

                val dto = event.toNewDto(vendorId = currentUid)
                val updated = client.postgrest["events"].update(dto) {
                    filter {
                        eq("id", id)
                        eq("vendor_id", currentUid)
                    }
                    select()
                }.decodeSingle<EventModel>()
                UiState.Success(updated)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun deleteEvent(id: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Hanya vendor yang masuk yang dapat menghapus event.")

                client.postgrest["events"].delete {
                    filter {
                        eq("id", id)
                        eq("vendor_id", currentUid)
                    }
                }
                UiState.Success("Event berhasil dihapus")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
}
