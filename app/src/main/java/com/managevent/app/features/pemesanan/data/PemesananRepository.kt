package com.managevent.app.features.pemesanan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

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

    private fun Throwable.toError(fallback: String) =
        UiState.Error(localizedMessage ?: fallback)

    suspend fun getRiwayatPemesanan(): UiState<List<TransaksiModel>> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Success(DemoOrderStore.getAll())

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
            e.toError("Gagal mengambil riwayat pemesanan")
        }
    }

    suspend fun getPemesananById(transaksiId: String): UiState<TransaksiModel> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                if (userId == null) {
                    val demoOrder = DemoOrderStore.find(transaksiId)
                        ?: return@withContext UiState.Error("Pesanan demo tidak ditemukan.")
                    return@withContext UiState.Success(demoOrder)
                }
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
                e.toError("Gagal mengambil detail pemesanan")
            }
        }
    }

    suspend fun checkout(transaksi: TransaksiModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                if (userId == null) {
                    DemoOrderStore.add(transaksi)
                    return@withContext UiState.Success("Pemesanan demo berhasil dilakukan")
                }
                client.postgrest["transaksi"].insert(transaksi.toNewDto(userId))
                UiState.Success("Pemesanan berhasil dilakukan")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toError("Gagal melakukan checkout")
            }
        }
    }

    suspend fun updateStatusPemesanan(transaksiId: String, status: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                if (userId == null) {
                    return@withContext if (DemoOrderStore.updateStatus(transaksiId, status)) {
                        UiState.Success("Status pemesanan berhasil diperbarui")
                    } else {
                        UiState.Error("Pesanan demo tidak ditemukan.")
                    }
                }
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
                e.toError("Gagal memperbarui status")
            }
        }
    }

    suspend fun batalkanPemesanan(transaksiId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = currentUserId()
                if (userId == null) {
                    return@withContext if (DemoOrderStore.remove(transaksiId)) {
                        UiState.Success("Pemesanan berhasil dibatalkan")
                    } else {
                        UiState.Error("Pesanan demo tidak ditemukan.")
                    }
                }
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
                e.toError("Gagal membatalkan pemesanan")
            }
        }
    }

    private object DemoOrderStore {
        private val lock = Any()
        private val orders = mutableListOf<TransaksiModel>()

        fun getAll(): List<TransaksiModel> = synchronized(lock) {
            orders.toList()
        }

        fun find(id: String): TransaksiModel? = synchronized(lock) {
            orders.firstOrNull { it.id == id }
        }

        fun add(transaction: TransaksiModel) = synchronized(lock) {
            transaction.copy(
                id = UUID.randomUUID().toString(),
                userId = "demo-user",
                createdAt = Instant.now().toString()
            ).also(orders::add)
        }

        fun updateStatus(id: String, status: String): Boolean = synchronized(lock) {
            val index = orders.indexOfFirst { it.id == id }
            if (index == -1) return@synchronized false
            orders[index] = orders[index].copy(status = status)
            true
        }

        fun remove(id: String): Boolean = synchronized(lock) {
            orders.removeAll { it.id == id }
        }
    }
}
