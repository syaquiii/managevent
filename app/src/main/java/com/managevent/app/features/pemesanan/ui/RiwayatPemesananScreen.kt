package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.components.StatusChip
import com.managevent.app.core.components.StatusType
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.Primary
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.PrimaryFixed
import com.managevent.app.core.theme.SecondaryFixed
import com.managevent.app.core.theme.Surface
import com.managevent.app.core.theme.SurfaceContainer
import com.managevent.app.core.theme.SurfaceContainerLow
import com.managevent.app.core.theme.SurfaceContainerLowest
import com.managevent.app.core.util.UiState
import com.managevent.app.features.pemesanan.data.TransaksiModel
import com.managevent.app.features.pemesanan.viewmodel.PemesananViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val orderStatuses = listOf("Berjalan", "Selesai")

@Composable
fun RiwayatPemesananScreen(
    modifier: Modifier = Modifier,
    viewModel: PemesananViewModel = viewModel(),
    onBackClick: (() -> Unit)? = null,
    onTransactionClick: (String) -> Unit = {}
) {
    val riwayatState by viewModel.riwayatState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchRiwayat()
    }

    RiwayatPemesananContent(
        riwayatState = riwayatState,
        onBackClick = onBackClick,
        onTransactionClick = onTransactionClick,
        modifier = modifier
    )
}

@Composable
fun RiwayatPemesananContent(
    riwayatState: UiState<List<TransaksiModel>>,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(pageCount = { orderStatuses.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = { AppTopBar(title = "Pesanan", onBackClick = onBackClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerLow)
                    .padding(4.dp)
            ) {
                orderStatuses.forEachIndexed { index, status ->
                    val selected = pagerState.currentPage == index
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .clickable {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                        color = if (selected) SurfaceContainerLowest else Color.Transparent,
                        shape = RoundedCornerShape(11.dp),
                        shadowElevation = if (selected) 1.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = status,
                                color = if (selected) PrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            TicketSyncBanner()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val status = orderStatuses[page]
                val transactions = when (riwayatState) {
                    is UiState.Success -> riwayatState.data.filter {
                        it.status.equals(status, ignoreCase = true)
                    }
                    else -> emptyList()
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (riwayatState) {
                        UiState.Idle, UiState.Loading -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary)
                            }
                        }
                        is UiState.Error -> item {
                            Text(
                                text = riwayatState.message,
                                modifier = Modifier.padding(vertical = 24.dp),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        is UiState.Success -> {
                            if (transactions.isEmpty()) {
                                item {
                                    EmptyOrdersState(status = status)
                                }
                            } else {
                                items(
                                    items = transactions,
                                    key = { it.id }
                                ) { transaction ->
                                    TransactionCard(
                                        transaction = transaction,
                                        onClick = { onTransactionClick(transaction.id) }
                                    )
                                }
                            }
                            if (status == "Selesai") {
                                item { ArchiveMemoryBanner() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketSyncBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(PrimaryFixed.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = "Tiket digital Anda tersinkronisasi otomatis dengan e-wallet.",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun TransactionCard(
    transaction: TransaksiModel,
    onClick: () -> Unit
) {
    val isOngoing = transaction.status.equals("Berjalan", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow.copy(alpha = 0.55f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "#MGV-${transaction.id.takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                StatusChip(
                    status = if (isOngoing) StatusType.BERJALAN else StatusType.SELESAI
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = transaction.eventImageUrl,
                    contentDescription = "Poster ${transaction.eventTitle}",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainer),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = transaction.eventTitle.ifBlank { "Event ${transaction.eventId.take(8)}" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = transaction.eventDate.ifBlank { "Tanggal event" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${transaction.jumlahTiket} Peserta",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Total Biaya",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatRupiah(transaction.totalHarga),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer
                    )
                }
                Surface(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick),
                    color = if (isOngoing) PrimaryFixed else SurfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isOngoing) "Lihat Detail" else "Lihat Invoice",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isOngoing) PrimaryContainer else Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (isOngoing) Icons.Default.ChevronRight else Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = if (isOngoing) PrimaryContainer else Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatRupiah(amount: Double): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }
    return "Rp ${numberFormat.format(amount)}"
}

@Composable
private fun EmptyOrdersState(status: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Belum ada pesanan $status",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Pesanan event Anda akan muncul di sini.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ArchiveMemoryBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceContainerLow)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SecondaryFixed),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HistoryEdu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Arsip Bersejarah Anda",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Seluruh momen dan tiket event sebelumnya tersimpan rapi.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiwayatPemesananContentPreview() {
    ManageventTheme {
        RiwayatPemesananContent(
            riwayatState = UiState.Success(
                listOf(
                    TransaksiModel(
                        id = "MGV2026094",
                        eventId = "evt-2",
                        jumlahTiket = 1,
                        totalHarga = 350000.0,
                        status = "Berjalan",
                        eventTitle = "Jakarta Jazz Symphony 2026",
                        eventDate = "22 Agustus 2026"
                    )
                )
            ),
            onTransactionClick = {}
        )
    }
}
