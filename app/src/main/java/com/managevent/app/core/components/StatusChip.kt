package com.managevent.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.managevent.app.core.theme.Error
import com.managevent.app.core.theme.ErrorContainer
import com.managevent.app.core.theme.StatusBerjalanBackground
import com.managevent.app.core.theme.StatusBerjalanText
import com.managevent.app.core.theme.StatusSelesaiBackground
import com.managevent.app.core.theme.StatusSelesaiText

enum class StatusType {
    BERJALAN,
    SELESAI,
    MENUNGGU_PEMBAYARAN,
    DIBATALKAN;

    companion object {
        fun fromStatus(status: String, paymentStatus: String? = null): StatusType {
            return when {
                status.equals("DIBATALKAN", ignoreCase = true) ||
                    paymentStatus.equals("DIBATALKAN", ignoreCase = true) -> DIBATALKAN
                status.equals("MENUNGGU_PEMBAYARAN", ignoreCase = true) ||
                    status.equals("Menunggu Pembayaran", ignoreCase = true) ||
                    paymentStatus.equals("PENDING", ignoreCase = true) ||
                    paymentStatus.equals("KEDALUWARSA", ignoreCase = true) -> MENUNGGU_PEMBAYARAN
                status.equals("SELESAI", ignoreCase = true) -> SELESAI
                else -> BERJALAN
            }
        }
    }
}

@Composable
fun StatusChip(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textLabel) = when (status) {
        StatusType.BERJALAN -> Triple(StatusBerjalanBackground, StatusBerjalanText, "Berjalan")
        StatusType.SELESAI -> Triple(StatusSelesaiBackground, StatusSelesaiText, "Selesai")
        StatusType.MENUNGGU_PEMBAYARAN -> Triple(Color(0xFFFFF3CD), Color(0xFF856404), "Menunggu Bayar")
        StatusType.DIBATALKAN -> Triple(ErrorContainer, Error, "Dibatalkan")
    }

    Box(
        modifier = modifier
            .height(28.dp)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textLabel,
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
