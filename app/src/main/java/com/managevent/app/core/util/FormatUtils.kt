package com.managevent.app.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val indonesianLocale: Locale = Locale.forLanguageTag("id-ID")

fun formatRupiah(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }
    return "Rp ${formatter.format(amount.toLong())}"
}

fun parseIsoInstant(isoString: String?): Instant? {
    if (isoString.isNullOrBlank()) return null
    val trimmed = isoString.trim()
    val normalized = trimmed.replace(" ", "T")

    return try {
        OffsetDateTime.parse(normalized).toInstant()
    } catch (_: Exception) {
        try {
            Instant.parse(normalized)
        } catch (_: Exception) {
            try {
                LocalDateTime.parse(normalized).atZone(ZoneId.systemDefault()).toInstant()
            } catch (_: Exception) {
                try {
                    LocalDate.parse(trimmed).atStartOfDay(ZoneId.systemDefault()).toInstant()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}

fun formatDateTimeId(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "-"
    val instant = parseIsoInstant(isoString) ?: return isoString
    val zdt = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm 'WIB'", indonesianLocale)
    return formatter.format(zdt)
}

fun formatDateId(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "-"
    val instant = parseIsoInstant(isoString) ?: return isoString
    val zdt = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", indonesianLocale)
    return formatter.format(zdt)
}

fun formatCountdown(totalSeconds: Long): String {
    if (totalSeconds <= 0) return "Kedaluwarsa"
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}
