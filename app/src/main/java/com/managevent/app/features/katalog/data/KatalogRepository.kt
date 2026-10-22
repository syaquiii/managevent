package com.managevent.app.features.katalog.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KatalogRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = client.postgrest["events"].select().decodeList<EventModel>()
                UiState.Success(events)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil data katalog event")
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
                UiState.Error(e.localizedMessage ?: "Gagal mengambil detail event")
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
}
