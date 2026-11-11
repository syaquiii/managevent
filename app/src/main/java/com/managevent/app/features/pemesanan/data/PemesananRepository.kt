package com.managevent.app.features.pemesanan.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

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
        val status: String = "MENUNGGU_PEMBAYARAN",
        @SerialName("status_pembayaran") val statusPembayaran: String = "PENDING",
        @SerialName("metode_pembayaran") val metodePembayaran: String = "bca_va",
        @SerialName("nomor_virtual_account") val nomorVirtualAccount: String? = null,
        @SerialName("kode_referensi") val kodeReferensi: String? = null,
        @SerialName("payment_expires_at") val paymentExpiresAt: String? = null,
        @SerialName("paid_at") val paidAt: String? = null,
        @SerialName("created_at") val createdAt: String = "",
        val events: EventSummaryDto? = null
    ) {
        fun toModel(): TransaksiModel {
            // Cek otomatis apakah sudah melewati batas waktu 24 jam dan masih PENDING
            val isExpired = try {
                if (statusPembayaran.equals("PENDING", ignoreCase = true) && !paymentExpiresAt.isNullOrBlank()) {
                    val exp = Instant.parse(paymentExpiresAt)
                    Instant.now().isAfter(exp)
                } else false
            } catch (_: Exception) { false }

            val effectiveStatusPembayaran = if (isExpired) "KEDALUWARSA" else statusPembayaran

            return TransaksiModel(
                id = id,
                userId = userId,
                eventId = eventId,
                jumlahTiket = jumlahTiket,
                catatan = catatan,
                totalHarga = totalHarga,
                status = status,
                statusPembayaran = effectiveStatusPembayaran,
                metodePembayaran = metodePembayaran,
                nomorVirtualAccount = nomorVirtualAccount,
                kodeReferensi = kodeReferensi,
                paymentExpiresAt = paymentExpiresAt,
                paidAt = paidAt,
                createdAt = createdAt,
                eventTitle = events?.title.orEmpty(),
                eventDate = events?.date.orEmpty(),
                eventImageUrl = events?.imageUrl
            )
        }
    }

    @Serializable
    private data class NewTransaksiDto(
        @SerialName("user_id") val userId: String,
        @SerialName("event_id") val eventId: String,
        @SerialName("jumlah_tiket") val jumlahTiket: Int,
        val catatan: String?,
        @SerialName("total_harga") val totalHarga: Double,
        val status: String = "MENUNGGU_PEMBAYARAN",
        @SerialName("status_pembayaran") val statusPembayaran: String = "PENDING",
        @SerialName("metode_pembayaran") val metodePembayaran: String = "bca_va",
        @SerialName("nomor_virtual_account") val nomorVirtualAccount: String,
        @SerialName("kode_referensi") val kodeReferensi: String,
        @SerialName("payment_expires_at") val paymentExpiresAt: String
    )

    private fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    private fun generateVaNumber(bankCode: String): String {
        val prefix = when (bankCode.lowercase()) {
            "bca", "bca_va" -> "88012"
            "bri", "bri_va" -> "10293"
            "bni", "bni_va" -> "98801"
            "mandiri", "mandiri_va" -> "89022"
            else -> "88000"
        }
        val randomDigits = (100000000..999999999).random(Random(System.currentTimeMillis()))
        return "$prefix$randomDigits"
    }

    private fun generateRefCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomStr = (1..8).map { chars.random() }.joinToString("")
        return "REF-$randomStr"
    }

    suspend fun checkout(transaksi: TransaksiModel): UiState<TransaksiModel> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            val expiresAt = Instant.now().plus(24, ChronoUnit.HOURS).toString()
            val vaNum = generateVaNumber(transaksi.metodePembayaran)
            val refCode = generateRefCode()

            val newDto = NewTransaksiDto(
                userId = userId,
                eventId = transaksi.eventId,
                jumlahTiket = transaksi.jumlahTiket,
                catatan = transaksi.catatan,
                totalHarga = transaksi.totalHarga,
                status = "MENUNGGU_PEMBAYARAN",
                statusPembayaran = "PENDING",
                metodePembayaran = transaksi.metodePembayaran.ifBlank { "bca_va" },
                nomorVirtualAccount = vaNum,
                kodeReferensi = refCode,
                paymentExpiresAt = expiresAt
            )

            val created = client.postgrest["transaksi"]
                .insert(newDto) {
                    select(Columns.raw("*, events(title, date, image_url)"))
                }
                .decodeSingle<TransaksiWithEventDto>()
                .toModel()

            UiState.Success(created)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    suspend fun processPayment(transaksiId: String, bankCode: String): UiState<TransaksiModel> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            val nowStr = Instant.now().toString()
            val updated = client.postgrest["transaksi"].update({
                set("status_pembayaran", "BERHASIL")
                set("status", "BERJALAN")
                set("metode_pembayaran", bankCode)
                set("paid_at", nowStr)
            }) {
                filter {
                    eq("id", transaksiId)
                    eq("user_id", userId)
                }
                select(Columns.raw("*, events(title, date, image_url)"))
            }.decodeSingle<TransaksiWithEventDto>().toModel()

            UiState.Success(updated)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    suspend fun renewPaymentInstruction(transaksiId: String, bankCode: String): UiState<TransaksiModel> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            val newExpiresAt = Instant.now().plus(24, ChronoUnit.HOURS).toString()
            val newVa = generateVaNumber(bankCode)
            val newRef = generateRefCode()

            val updated = client.postgrest["transaksi"].update({
                set("status_pembayaran", "PENDING")
                set("status", "MENUNGGU_PEMBAYARAN")
                set("metode_pembayaran", bankCode)
                set("nomor_virtual_account", newVa)
                set("kode_referensi", newRef)
                set("payment_expires_at", newExpiresAt)
            }) {
                filter {
                    eq("id", transaksiId)
                    eq("user_id", userId)
                }
                select(Columns.raw("*, events(title, date, image_url)"))
            }.decodeSingle<TransaksiWithEventDto>().toModel()

            UiState.Success(updated)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error(e.toUserFriendlyMessage())
        }
    }

    suspend fun getRiwayatPemesanan(): UiState<List<TransaksiModel>> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Success(emptyList())

        try {
            val list = client.postgrest["transaksi"]
                .select(Columns.raw("*, events(title, date, image_url)")) {
                    filter { eq("user_id", userId) }
                    order("created_at", order = Order.DESCENDING)
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

    suspend fun getPemesananById(transaksiId: String): UiState<TransaksiModel> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
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

    suspend fun updateStatusPemesanan(transaksiId: String, status: String): UiState<String> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
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

    suspend fun batalkanPemesanan(transaksiId: String): UiState<String> = withContext(Dispatchers.IO) {
        val userId = currentUserId()
            ?: return@withContext UiState.Error("Silakan masuk terlebih dahulu.")

        try {
            client.postgrest["transaksi"].update({
                set("status", "DIBATALKAN")
            }) {
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