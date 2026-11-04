package com.managevent.app.features.ulasan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun addUlasan(ulasan: UlasanModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["ulasan"].insert(ulasan)
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
