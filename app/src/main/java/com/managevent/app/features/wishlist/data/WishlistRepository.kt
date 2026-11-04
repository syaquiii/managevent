package com.managevent.app.features.wishlist.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WishlistRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getWishlistEvents(userId: String): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val wishlistRows = client.postgrest["wishlist"].select {
                    filter { eq("user_id", userId) }
                }.decodeList<WishlistModel>()

                val eventIds = wishlistRows.map { it.eventId }
                if (eventIds.isEmpty()) {
                    return@withContext UiState.Success(emptyList())
                }

                val events = client.postgrest["events"].select {
                    filter { isIn("id", eventIds) }
                }.decodeList<EventModel>().map { it.copy(isFavorite = true) }

                UiState.Success(events)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil daftar favorit")
            }
        }
    }

    suspend fun addToWishlist(userId: String, eventId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val model = WishlistModel(userId = userId, eventId = eventId)
                client.postgrest["wishlist"].insert(model)
                UiState.Success("Ditambahkan ke favorit")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menambah ke favorit")
            }
        }
    }

    suspend fun removeFromWishlist(userId: String, eventId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["wishlist"].delete {
                    filter {
                        eq("user_id", userId)
                        eq("event_id", eventId)
                    }
                }
                UiState.Success("Dihapus dari favorit")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menghapus dari favorit")
            }
        }
    }
}
