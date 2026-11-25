package com.managevent.app.features.ulasan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

open class UlasanRepository(
    private val clientProvider: () -> SupabaseClient = { SupabaseClientProvider.client }
) {
    private val client: SupabaseClient by lazy(clientProvider)

    open suspend fun getUlasanByEvent(eventId: String): UiState<List<UlasanModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = client.postgrest["ulasan"].select {
                    filter { eq("event_id", eventId) }
                    order("created_at", order = Order.DESCENDING)
                }.decodeList<UlasanModel>()
                UiState.Success(list)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun addUlasan(
        eventId: String,
        userId: String,
        username: String,
        rating: Int,
        comment: String
    ): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val activeUid = userId.ifBlank { client.auth.currentUserOrNull()?.id.orEmpty() }
                if (activeUid.isBlank()) {
                    return@withContext UiState.Error("Silakan masuk terlebih dahulu untuk memberi ulasan.")
                }
                val trimmedComment = comment.trim()
                if (trimmedComment.isBlank()) {
                    return@withContext UiState.Error("Komentar ulasan tidak boleh kosong.")
                }

                val newUlasan = buildJsonObject {
                    put("event_id", eventId)
                    put("user_id", activeUid)
                    put("username", username.ifBlank { "Pengguna" })
                    put("rating", rating.coerceIn(1, 5))
                    put("comment", trimmedComment)
                }

                client.postgrest["ulasan"].insert(newUlasan)
                UiState.Success("Ulasan berhasil dikirim")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun updateUlasan(ulasanId: String, comment: String, rating: Int): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val activeUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                val trimmedComment = comment.trim()
                if (trimmedComment.isBlank()) {
                    return@withContext UiState.Error("Komentar ulasan tidak boleh kosong.")
                }

                client.postgrest["ulasan"].update({
                    set("comment", trimmedComment)
                    set("rating", rating.coerceIn(1, 5))
                }) {
                    filter {
                        eq("id", ulasanId)
                        eq("user_id", activeUid)
                    }
                }
                UiState.Success("Ulasan berhasil diperbarui")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun deleteUlasan(ulasanId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val activeUid = client.auth.currentUserOrNull()?.id
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

                client.postgrest["ulasan"].delete {
                    filter {
                        eq("id", ulasanId)
                        eq("user_id", activeUid)
                    }
                }
                UiState.Success("Ulasan berhasil dihapus")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
}
