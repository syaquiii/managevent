package com.managevent.app.features.wishlist.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import com.managevent.app.features.katalog.data.EventModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class WishlistRepository {
    private val client = SupabaseClientProvider.client

    @Serializable
    private data class NewWishlistDto(
        @SerialName("user_id") val userId: String,
        @SerialName("event_id") val eventId: String
    )

    private fun isValidUuid(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getWishlistEvents(userIdInput: String): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val uid = userIdInput.ifBlank { client.auth.currentUserOrNull()?.id.orEmpty() }
                if (uid.isBlank() || !isValidUuid(uid)) {
                    return@withContext UiState.Success(emptyList())
                }

                val wishlistRows = client.postgrest["wishlist"].select {
                    filter { eq("user_id", uid) }
                }.decodeList<WishlistModel>()

                val eventIds = wishlistRows.map { it.eventId }.toSet()
                if (eventIds.isEmpty()) {
                    return@withContext UiState.Success(emptyList())
                }

                val events = client.postgrest["events"].select {
                    filter { isIn("id", eventIds.toList()) }
                }.decodeList<EventModel>().map { it.copy(isFavorite = true) }

                UiState.Success(events)
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun addToWishlist(userIdInput: String, eventId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val uid = userIdInput.ifBlank { client.auth.currentUserOrNull()?.id.orEmpty() }
                if (uid.isBlank() || !isValidUuid(uid)) {
                    return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                }
                if (!isValidUuid(eventId)) {
                    return@withContext UiState.Error("Event tidak ditemukan di database.")
                }

                val dto = NewWishlistDto(userId = uid, eventId = eventId)
                client.postgrest["wishlist"].insert(dto)
                UiState.Success("Ditambahkan ke favorit")
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun removeFromWishlist(userIdInput: String, eventId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val uid = userIdInput.ifBlank { client.auth.currentUserOrNull()?.id.orEmpty() }
                if (uid.isBlank() || !isValidUuid(uid)) {
                    return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                }
                client.postgrest["wishlist"].delete {
                    filter {
                        eq("user_id", uid)
                        eq("event_id", eventId)
                    }
                }
                UiState.Success("Dihapus dari favorit")
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
}
