package com.managevent.app.features.ulasan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UlasanRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getUlasanByEvent(eventId: String): UiState<List<UlasanModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = client.postgrest["ulasan"].select {
                    filter { eq("event_id", eventId) }
                }.decodeList<UlasanModel>()
                UiState.Success(list)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil ulasan")
            }
        }
    }

    suspend fun addUlasan(eventId: String, userId: String, username: String, rating: Int, comment: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Ubah mapOf menjadi buildJsonObject biar servernya ngerti
                val newUlasan = buildJsonObject {
                    put("event_id", eventId)
                    put("user_id", userId)
                    put("username", username)
                    put("rating", rating)
                    put("comment", comment)
                }

                client.postgrest["ulasan"].insert(newUlasan)
                UiState.Success("Ulasan berhasil dikirim")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengirim ulasan")
            }
        }
    }

    suspend fun updateUlasan(ulasanId: String, comment: String, rating: Int): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ulasan"].update({
                    set("comment", comment)
                    set("rating", rating)
                }) {
                    filter { eq("id", ulasanId) }
                }
                UiState.Success("Ulasan berhasil diperbarui")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal memperbarui ulasan")
            }
        }
    }

    suspend fun deleteUlasan(ulasanId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ulasan"].delete {
                    filter { eq("id", ulasanId) }
                }
                UiState.Success("Ulasan berhasil dihapus")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menghapus ulasan")
            }
        }
    }
}