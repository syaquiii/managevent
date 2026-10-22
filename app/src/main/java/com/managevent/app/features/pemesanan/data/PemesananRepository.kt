package com.managevent.app.features.pemesanan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PemesananRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getRiwayatPemesanan(userId: String): UiState<List<TransaksiModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = client.postgrest["transaksi"].select {
                    filter { eq("user_id", userId) }
                }.decodeList<TransaksiModel>()
                UiState.Success(list)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil riwayat pemesanan")
            }
        }
    }

    suspend fun checkout(transaksi: TransaksiModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["transaksi"].insert(transaksi)
                UiState.Success("Pemesanan berhasil dilakukan")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal melakukan checkout")
            }
        }
    }
}
