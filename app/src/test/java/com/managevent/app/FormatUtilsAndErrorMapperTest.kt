package com.managevent.app

import com.managevent.app.core.components.StatusType
import com.managevent.app.core.util.formatCountdown
import com.managevent.app.core.util.formatDateId
import com.managevent.app.core.util.formatDateTimeId
import com.managevent.app.core.util.formatRupiah
import com.managevent.app.core.util.parseIsoInstant
import com.managevent.app.core.util.parseRawErrorMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatUtilsAndErrorMapperTest {

    @Test
    fun parseIsoInstant_supportsSupabaseOffsetAndZulu() {
        val offsetInstant = parseIsoInstant("2026-11-18T09:06:55.516724+00:00")
        val zuluInstant = parseIsoInstant("2026-11-18T09:06:55.516724Z")
        val spacedInstant = parseIsoInstant("2026-11-18 09:06:55+00:00")

        assertNotNull(offsetInstant)
        assertEquals(offsetInstant, zuluInstant)
        assertNotNull(spacedInstant)
        assertNull(parseIsoInstant(""))
        assertNull(parseIsoInstant("not-a-date"))
    }

    @Test
    fun formatHelpers_formatIndonesianCurrencyAndCountdown() {
        assertEquals("Rp 150.000", formatRupiah(150000.0))
        assertEquals("Rp 0", formatRupiah(0.0))
        assertEquals("Kedaluwarsa", formatCountdown(0))
        assertEquals("Kedaluwarsa", formatCountdown(-15))
        assertEquals("01:01:05", formatCountdown(3665))
        assertEquals("-", formatDateTimeId(""))
        assertEquals("-", formatDateId(null))
        assertTrue(formatDateId("2026-11-18T09:06:55+00:00").contains("2026"))
    }

    @Test
    fun errorMessageMapper_mapsSupabaseErrorsToUserFriendlyMessages() {
        assertEquals(
            "Email atau password yang Anda masukkan salah.",
            parseRawErrorMessage("Invalid login credentials")
        )
        assertEquals(
            "Email Anda belum dikonfirmasi. Silakan cek kotak masuk email Anda.",
            parseRawErrorMessage("email_not_confirmed")
        )
        assertEquals(
            "Anda tidak memiliki izin untuk melakukan tindakan ini.",
            parseRawErrorMessage("new row violates row-level security policy (42501)")
        )
        assertEquals(
            "Skema database belum sinkron. Jalankan docs/supabase_schema.sql di Supabase.",
            parseRawErrorMessage("PGRST204: Could not find the 'vendor_id' column")
        )
    }

    @Test
    fun statusType_mapsAllOrderStatesAccurately() {
        assertEquals(StatusType.MENUNGGU_PEMBAYARAN, StatusType.fromStatus("MENUNGGU_PEMBAYARAN", "PENDING"))
        assertEquals(StatusType.MENUNGGU_PEMBAYARAN, StatusType.fromStatus("MENUNGGU_PEMBAYARAN", "KEDALUWARSA"))
        assertEquals(StatusType.BERJALAN, StatusType.fromStatus("BERJALAN", "BERHASIL"))
        assertEquals(StatusType.SELESAI, StatusType.fromStatus("Selesai", "BERHASIL"))
        assertEquals(StatusType.DIBATALKAN, StatusType.fromStatus("DIBATALKAN", "BERHASIL"))
    }
}
