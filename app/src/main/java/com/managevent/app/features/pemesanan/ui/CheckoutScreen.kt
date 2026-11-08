package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.Primary
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.PrimaryFixed
import com.managevent.app.core.theme.Surface
import com.managevent.app.core.theme.SurfaceContainer
import com.managevent.app.core.theme.SurfaceContainerLow
import com.managevent.app.core.theme.SurfaceContainerLowest
import com.managevent.app.core.theme.SurfaceContainerHighest
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import java.text.NumberFormat
import java.util.Locale

private const val MAX_TICKETS_PER_ORDER = 10

@Composable
fun CheckoutScreen(
    eventId: String,
    eventState: UiState<EventModel>,
    checkoutState: UiState<String>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSubmitCheckout: (Int, String, EventModel) -> Unit = { _, _, _ -> }
) {
    var jumlahTiket by rememberSaveable(eventId) { mutableIntStateOf(1) }
    var catatan by rememberSaveable(eventId) { mutableStateOf("") }

    CheckoutContent(
        eventState = eventState,
        jumlahTiket = jumlahTiket,
        catatan = catatan,
        checkoutState = checkoutState,
        onBackClick = onBackClick,
        onDecreaseTickets = { jumlahTiket = (jumlahTiket - 1).coerceAtLeast(1) },
        onIncreaseTickets = { jumlahTiket = (jumlahTiket + 1).coerceAtMost(MAX_TICKETS_PER_ORDER) },
        onNotesChange = { catatan = it.take(500) },
        onConfirmOrder = {
            val event = (eventState as? UiState.Success)?.data ?: return@CheckoutContent
            onSubmitCheckout(jumlahTiket, catatan, event)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    eventState: UiState<EventModel>,
    jumlahTiket: Int,
    catatan: String,
    checkoutState: UiState<String>,
    onBackClick: () -> Unit,
    onDecreaseTickets: () -> Unit,
    onIncreaseTickets: () -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirmOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val event = (eventState as? UiState.Success)?.data
    val totalHarga = (event?.price ?: 0.0) * jumlahTiket
    val canSubmit = event != null && checkoutState !is UiState.Loading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout Pesanan",
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
            Surface(color = Surface, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    AppButton(
                        text = if (checkoutState is UiState.Loading) "Memproses..." else "Konfirmasi Pesanan  →",
                        onClick = onConfirmOrder,
                        enabled = canSubmit,
                        variant = AppButtonVariant.CONVERSION_CTA
                    )
                }
            }
        }
    ) { innerPadding ->
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
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryContainer,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Langkah Terakhir",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Konfirmasi detail tiket sebelum melanjutkan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "2/2",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainerHighest)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = PrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            when (eventState) {
                UiState.Idle, UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingScreen()
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = eventState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is UiState.Success -> {
                    EventSummaryCard(event = eventState.data)

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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Jumlah Peserta",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Maksimal 10 tiket per transaksi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TicketQuantityButton(
                                    icon = Icons.Default.Remove,
                                    description = "Kurangi jumlah tiket",
                                    enabled = jumlahTiket > 1,
                                    onClick = onDecreaseTickets,
                                    backgroundColor = SurfaceContainerLow,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = jumlahTiket.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                TicketQuantityButton(
                                    icon = Icons.Default.Add,
                                    description = "Tambah jumlah tiket",
                                    enabled = jumlahTiket < MAX_TICKETS_PER_ORDER,
                                    onClick = onIncreaseTickets,
                                    backgroundColor = PrimaryFixed,
                                    tint = PrimaryContainer
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = catatan,
                            onValueChange = onNotesChange,
                            placeholder = {
                                Text("Tambahkan catatan khusus (opsional)")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 3,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow,
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Ringkasan Biaya",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Harga Tiket ($jumlahTiket × ${formatRupiah(eventState.data.price)})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatRupiah(totalHarga),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            androidx.compose.material3.HorizontalDivider(color = SurfaceContainerHighest)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Harga",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Termasuk pajak & layanan",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = formatRupiah(totalHarga),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryContainer
                                )
                            }
                        }
                    }

                    when (checkoutState) {
                        is UiState.Error -> Text(
                            text = checkoutState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        else -> Unit
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun EventSummaryCard(
    event: EventModel
) {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "Poster ${event.title}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainer),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = PrimaryContainer
                    )
                    Text(
                        text = "Tiket Masuk Reguler",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Text(
                    text = event.date.ifBlank { "Tanggal belum ditentukan" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatRupiah(event.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
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
private fun TicketQuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    tint: Color
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) backgroundColor else SurfaceContainer)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) tint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutContentPreview() {
    ManageventTheme {
        CheckoutContent(
            eventState = UiState.Success(
                EventModel(
                    id = "event-preview",
                    title = "Pameran Pernikahan Nusantara 2026",
                    date = "18 Juli 2026",
                    price = 150000.0,
                    imageUrl = null
                )
            ),
            jumlahTiket = 1,
            catatan = "",
            checkoutState = UiState.Idle,
            onBackClick = {},
            onDecreaseTickets = {},
            onIncreaseTickets = {},
            onNotesChange = {},
            onConfirmOrder = {}
        )
    }
}
