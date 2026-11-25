package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.components.StatusChip
import com.managevent.app.core.components.StatusType
import com.managevent.app.core.theme.ErrorContainer
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnErrorContainer
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.Surface
import com.managevent.app.core.theme.SurfaceContainer
import com.managevent.app.core.theme.SurfaceContainerLow
import com.managevent.app.core.theme.SurfaceContainerLowest
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.formatDateTimeId
import com.managevent.app.core.util.formatRupiah
import com.managevent.app.features.pemesanan.data.TransaksiModel

@Composable
fun DetailPemesananScreen(
    detailState: UiState<TransaksiModel>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMarkFinished: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onCancelWithReason: (String) -> Unit = {},
    onNavigateToPayment: (String) -> Unit = {}
) {
    DetailPemesananContent(
        detailState = detailState,
        onBackClick = onBackClick,
        onMarkFinished = onMarkFinished,
        onCancelClick = onCancelClick,
        onCancelWithReason = onCancelWithReason,
        onNavigateToPayment = onNavigateToPayment,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPemesananContent(
    detailState: UiState<TransaksiModel>,
    onBackClick: () -> Unit,
    onMarkFinished: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCancelWithReason: (String) -> Unit = {},
    onNavigateToPayment: (String) -> Unit = {}
) {
    val transaction = (detailState as? UiState.Success)?.data
    val statusType = transaction?.let { StatusType.fromStatus(it.status, it.statusPembayaran) }
    val isOngoing = statusType == StatusType.BERJALAN
    val isUnpaid = statusType == StatusType.MENUNGGU_PEMBAYARAN
    val isCancelled = statusType == StatusType.DIBATALKAN

    var showRefundDialog by remember { mutableStateOf(false) }
    val refundReasons = listOf("Perubahan jadwal / acara", "Salah memilih jenis tiket", "Kendala pribadi / darurat", "Lainnya")
    var selectedReason by remember { mutableStateOf(refundReasons[0]) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Pesanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            if (transaction != null && !isCancelled) {
                if (isUnpaid) {
                    Surface(color = Surface, shadowElevation = 8.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AppButton(
                                text = if (transaction.statusPembayaran.equals("KEDALUWARSA", ignoreCase = true)) "Buat Instruksi Pembayaran Baru →" else "Lanjutkan Pembayaran →",
                                onClick = { onNavigateToPayment(transaction.id) },
                                variant = AppButtonVariant.CONVERSION_CTA
                            )
                            AppButton(
                                text = "Batalkan Pesanan",
                                onClick = onCancelClick,
                                variant = AppButtonVariant.SECONDARY_OUTLINED
                            )
                        }
                    }
                } else if (isOngoing) {
                    Surface(color = Surface, shadowElevation = 8.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AppButton(
                                text = "Tandai Selesai",
                                onClick = onMarkFinished,
                                variant = AppButtonVariant.CONVERSION_CTA
                            )
                            AppButton(
                                text = "Batalkan Pesanan (Refund)",
                                onClick = { showRefundDialog = true },
                                variant = AppButtonVariant.SECONDARY_OUTLINED
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when (detailState) {
            UiState.Idle, UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = detailState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            is UiState.Success -> {
                val order = detailState.data
                val orderStatusType = StatusType.fromStatus(order.status, order.statusPembayaran)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = PrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ID Pesanan",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "#MGV-${order.id.takeLast(6).uppercase()}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        StatusChip(status = orderStatusType)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            AsyncImage(
                                model = order.eventImageUrl,
                                contentDescription = "Poster ${order.eventTitle}",
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainer),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = order.eventTitle.ifBlank {
                                        "Event ${order.eventId.take(8)}"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                DetailIconLabel(
                                    icon = Icons.Default.CalendarToday,
                                    text = order.eventDate.ifBlank { "Tanggal event" }
                                )
                                DetailIconLabel(
                                    icon = Icons.Default.ConfirmationNumber,
                                    text = "${order.jumlahTiket} Peserta"
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Rincian Pembayaran",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            OrderDetailRow(
                                label = "Tanggal pemesanan",
                                value = formatDateTimeId(order.createdAt)
                            )
                            OrderDetailRow(
                                label = "Metode pembayaran",
                                value = formatBankName(order.metodePembayaran)
                            )
                            if (!order.kodeReferensi.isNullOrBlank()) {
                                OrderDetailRow(
                                    label = "Kode referensi",
                                    value = order.kodeReferensi
                                )
                            }
                            OrderDetailRow(
                                label = "Jumlah tiket",
                                value = "${order.jumlahTiket} tiket"
                            )
                            OrderDetailRow(
                                label = "Catatan",
                                value = order.catatan?.takeIf { it.isNotBlank() } ?: "-"
                            )
                            HorizontalDivider(color = SurfaceContainer)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Biaya",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formatRupiah(order.totalHarga),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryContainer
                                )
                            }
                        }
                    }

                    when (orderStatusType) {
                        StatusType.SELESAI -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pesanan ini telah selesai.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        StatusType.DIBATALKAN -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ErrorContainer)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pesanan ini telah dibatalkan.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        StatusType.MENUNGGU_PEMBAYARAN -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Silakan selesaikan pembayaran untuk mengaktifkan tiket Anda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        StatusType.BERJALAN -> Unit
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showRefundDialog) {
        AlertDialog(
            onDismissRequest = { showRefundDialog = false },
            title = {
                Text(
                    text = "Alasan Pembatalan & Refund",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Silakan pilih alasan pembatalan pesanan Anda:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    refundReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason }
                            )
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                AppButton(
                    text = "Ajukan Refund",
                    onClick = {
                        showRefundDialog = false
                        onCancelWithReason(selectedReason)
                    },
                    variant = AppButtonVariant.CONVERSION_CTA
                )
            },
            dismissButton = {
                TextButton(onClick = { showRefundDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

private fun formatBankName(code: String): String {
    return when (code.lowercase()) {
        "bca_va", "bca" -> "BCA Virtual Account"
        "bri_va", "bri" -> "BRI Virtual Account"
        "bni_va", "bni" -> "BNI Virtual Account"
        "mandiri_va", "mandiri" -> "Mandiri Virtual Account"
        else -> code.ifBlank { "-" }
    }
}

@Composable
private fun DetailIconLabel(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OrderDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPemesananContentPreview() {
    ManageventTheme {
        DetailPemesananContent(
            detailState = UiState.Success(
                TransaksiModel(
                    id = "trx-101",
                    userId = "demo-user-id",
                    eventId = "evt-1",
                    jumlahTiket = 2,
                    catatan = "Butuh kursi dekat panggung.",
                    totalHarga = 640000.0,
                    status = "Berjalan",
                    statusPembayaran = "BERHASIL",
                    createdAt = "2026-11-05T10:15:00+00:00",
                    eventTitle = "Jakarta Jazz Symphony 2026",
                    eventDate = "22 Agustus 2026"
                )
            ),
            onBackClick = {},
            onMarkFinished = {},
            onCancelClick = {}
        )
    }
}
