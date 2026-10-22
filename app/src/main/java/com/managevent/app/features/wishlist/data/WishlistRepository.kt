package com.managevent.app.features.wishlist.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WishlistRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getWishlist(userId: String): UiState<List<WishlistModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = client.postgrest["wishlist"].select {
                    filter { eq("user_id", userId) }
                }.decodeList<WishlistModel>()
                UiState.Success(list)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil daftar wishlist")
            }
        }
    }

    suspend fun addToWishlist(wishlist: WishlistModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["wishlist"].insert(wishlist)
                UiState.Success("Ditambahkan ke wishlist")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menambah ke wishlist")
            }
        }
    }
}
