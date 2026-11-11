package com.managevent.app.features.pemesanan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PemesananRepository {
    private val client = SupabaseClientProvider.client

    @Serializable
    private data class EventSummaryDto(
        val title: String = "",
        val date: String = "",
        @SerialName("image_url") val imageUrl: String? = null
    )

    @Serializable
    private data class TransaksiWithEventDto(
        val id: String = "",
        @SerialName("user_id") val userId: String = "",
        @SerialName("event_id") val eventId: String = "",
        @SerialName("jumlah_tiket") val jumlahTiket: Int = 1,
        val catatan: String? = null,
        @SerialName("total_harga") val totalHarga: Double = 0.0,
        val status: String = "Berjalan",
        @SerialName("created_at") val createdAt: String = "",
        val events: EventSummaryDto? = null
    ) {
        fun toModel() = TransaksiModel(
            id = id,
            userId = userId,
            eventId = eventId,
            jumlahTiket = jumlahTiket,
            catatan = catatan,
            totalHarga = totalHarga,
            status = if (status.equals("Selesai", ignoreCase = true)) "Selesai" else "Berjalan",
            createdAt = createdAt,
            eventTitle = events?.title.orEmpty(),
            eventDate = events?.date.orEmpty(),
            eventImageUrl = events?.imageUrl
        )
    }

    @Serializable
    private data class NewTransaksiDto(
        @SerialName("user_id") val userId: String,
        @SerialName("event_id") val eventId: String,
        @SerialName("jumlah_tiket") val jumlahTiket: Int,
        val catatan: String?,
        @SerialName("total_harga") val totalHarga: Double,
        val status: String
    )

    private fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    private fun TransaksiModel.toNewDto(userId: String) = NewTransaksiDto(
        userId = userId,
        eventId = eventId,
        jumlahTiket = jumlahTiket,
        catatan = catatan,
        totalHarga = totalHarga,
        status = status
    )

    suspend fun getRiwayatPemesanan(): UiState<List<TransaksiModel>> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Success(emptyList())

        try {
            val list = client.postgrest["transaksi"]
                .select(Columns.raw("*, events(title, date, image_url)")) {
                    filter { eq("user_id", userId) }
                }
                .decodeList<TransaksiWithEventDto>()
                .map(TransaksiWithEventDto::toModel)
            UiState.Success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    suspend fun getPemesananById(transaksiId: String): UiState<TransaksiModel> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                val item = client.postgrest["transaksi"]
                    .select(Columns.raw("*, events(title, date, image_url)")) {
                        filter {
                            eq("id", transaksiId)
                            eq("user_id", userId)
                        }
                    }
                    .decodeSingle<TransaksiWithEventDto>()
                    .toModel()
                UiState.Success(item)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun checkout(transaksi: TransaksiModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                client.postgrest["transaksi"].insert(transaksi.toNewDto(userId))
                UiState.Success("Pemesanan berhasil dilakukan")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun updateStatusPemesanan(transaksiId: String, status: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                client.postgrest["transaksi"].update({
                    set("status", status)
                }) {
                    filter {
                        eq("id", transaksiId)
                        eq("user_id", userId)
                    }
                }
                UiState.Success("Status pemesanan berhasil diperbarui")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun batalkanPemesanan(transaksiId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                    ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                client.postgrest["transaksi"].delete {
                    filter {
                        eq("id", transaksiId)
                        eq("user_id", userId)
                    }
                }
                UiState.Success("Pemesanan berhasil dibatalkan")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
}
