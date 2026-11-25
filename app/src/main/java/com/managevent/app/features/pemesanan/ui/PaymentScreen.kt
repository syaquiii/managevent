package com.managevent.app.features.pemesanan.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.Primary
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.PrimaryFixed
import com.managevent.app.core.theme.Surface
import com.managevent.app.core.theme.SurfaceContainerLow
import com.managevent.app.core.theme.SurfaceContainerLowest
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.formatCountdown
import com.managevent.app.core.util.formatRupiah
import com.managevent.app.core.util.parseIsoInstant
import com.managevent.app.features.pemesanan.data.TransaksiModel
import kotlinx.coroutines.delay
import java.time.Instant

data class BankOption(
    val code: String,
    val name: String,
    val vaPrefix: String
)

private val bankList = listOf(
    BankOption("bca_va", "BCA Virtual Account", "88012"),
    BankOption("bri_va", "BRI Virtual Account", "10293"),
    BankOption("bni_va", "BNI Virtual Account", "98801"),
    BankOption("mandiri_va", "Mandiri Virtual Account", "89022")
)

@Composable
fun PaymentScreen(
    detailState: UiState<TransaksiModel>,
    paymentState: UiState<TransaksiModel>,
    paymentLoadingMessage: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onPayClick: (bankCode: String) -> Unit = {},
    onRenewClick: (bankCode: String) -> Unit = {},
    onCancelClick: () -> Unit = {},
    onNavigateToDetail: (transaksiId: String) -> Unit = {}
) {
    PaymentContent(
        detailState = detailState,
        paymentState = paymentState,
        paymentLoadingMessage = paymentLoadingMessage,
        onBackClick = onBackClick,
        onPayClick = onPayClick,
        onRenewClick = onRenewClick,
        onCancelClick = onCancelClick,
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentContent(
    detailState: UiState<TransaksiModel>,
    paymentState: UiState<TransaksiModel>,
    paymentLoadingMessage: String,
    onBackClick: () -> Unit,
    onPayClick: (bankCode: String) -> Unit,
    onRenewClick: (bankCode: String) -> Unit,
    onCancelClick: () -> Unit,
    onNavigateToDetail: (transaksiId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val transaksi = (detailState as? UiState.Success)?.data
    var selectedBank by remember(transaksi?.id, transaksi?.metodePembayaran) {
        mutableStateOf(
            bankList.firstOrNull { it.code == transaksi?.metodePembayaran } ?: bankList.first()
        )
    }
    val context = LocalContext.current
    var showSuccessDialog by remember(transaksi?.id) { mutableStateOf(false) }

    val isExpired = transaksi?.statusPembayaran.equals("KEDALUWARSA", ignoreCase = true)
    val isPaid = transaksi?.statusPembayaran.equals("BERHASIL", ignoreCase = true)

    var secondsLeft by remember(transaksi?.paymentExpiresAt) { mutableLongStateOf(0L) }
    LaunchedEffect(transaksi?.paymentExpiresAt) {
        val exp = parseIsoInstant(transaksi?.paymentExpiresAt)
        if (exp == null) {
            secondsLeft = 0L
            return@LaunchedEffect
        }
        while (true) {
            val diff = exp.epochSecond - Instant.now().epochSecond
            secondsLeft = diff.coerceAtLeast(0L)
            if (secondsLeft <= 0L) break
            delay(1000)
        }
    }

    LaunchedEffect(paymentState) {
        if (paymentState is UiState.Success && paymentState.data.statusPembayaran.equals("BERHASIL", ignoreCase = true)) {
            showSuccessDialog = true
        }
    }

    fun copyToClipboard(label: String, value: String, toastMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, value))
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pembayaran",
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
            if (transaksi != null && !isPaid) {
                Surface(color = Surface, shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isExpired || (transaksi.paymentExpiresAt != null && secondsLeft <= 0L)) {
                            AppButton(
                                text = "Buat Instruksi Pembayaran Baru",
                                onClick = { onRenewClick(selectedBank.code) },
                                enabled = paymentState !is UiState.Loading,
                                variant = AppButtonVariant.CONVERSION_CTA
                            )
                        } else {
                            AppButton(
                                text = if (paymentState is UiState.Loading) "Memproses..." else "Bayar Sekarang →",
                                onClick = { onPayClick(selectedBank.code) },
                                enabled = paymentState !is UiState.Loading,
                                variant = AppButtonVariant.CONVERSION_CTA
                            )
                        }
                        AppButton(
                            text = "Batalkan Pembayaran",
                            onClick = onCancelClick,
                            enabled = paymentState !is UiState.Loading,
                            variant = AppButtonVariant.SECONDARY_OUTLINED
                        )
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
                val activeTransaksi = detailState.data
                val hasExpired = isExpired || (activeTransaksi.paymentExpiresAt != null && secondsLeft <= 0L && !isPaid)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryFixed.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Silakan melakukan transfer sesuai dengan nomor Virtual Account dan nominal yang tertera di bawah ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (!isPaid && !hasExpired) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryFixed)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = PrimaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Batas Waktu Pembayaran",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryContainer
                                        )
                                        Text(
                                            text = formatCountdown(secondsLeft),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (hasExpired) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Instruksi pembayaran ini telah KEDALUWARSA. Silakan buat instruksi pembayaran baru.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.SemiBold
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Detail Transfer Virtual Account",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            val vaText = activeTransaksi.nomorVirtualAccount.orEmpty().ifBlank { "-" }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Nomor Virtual Account (${selectedBank.name})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vaText,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryContainer,
                                        letterSpacing = 1.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            copyToClipboard("Virtual Account", vaText, "Nomor Virtual Account berhasil disalin")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Salin Nomor VA",
                                            tint = Primary
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Biaya",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatRupiah(activeTransaksi.totalHarga),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryContainer
                                )
                            }

                            val refText = activeTransaksi.kodeReferensi.orEmpty().ifBlank { "-" }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Kode Referensi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = refText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    IconButton(
                                        onClick = {
                                            copyToClipboard("Kode Referensi", refText, "Kode Referensi berhasil disalin")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Salin Kode Referensi",
                                            tint = Primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!isPaid) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Pilih Metode Bank Virtual Account",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                bankList.forEach { bank ->
                                    val isSelected = selectedBank.code == bank.code
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) PrimaryFixed.copy(alpha = 0.5f) else SurfaceContainerLow)
                                            .clickable { selectedBank = bank }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (isSelected) PrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = bank.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedBank = bank },
                                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryContainer)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = paymentState is UiState.Loading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                PulsingCircularProgress()
                                Text(
                                    text = paymentLoadingMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryContainer
                                )
                            }
                        }
                    }

                    if (paymentState is UiState.Error) {
                        Text(
                            text = paymentState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showSuccessDialog && transaksi != null) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(56.dp)
                )
            },
            title = {
                Text(
                    text = "Pembayaran Berhasil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Terima kasih! Pembayaran Anda sebesar ${formatRupiah(transaksi.totalHarga)} melalui ${selectedBank.name} telah terverifikasi.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Status pesanan Anda kini aktif (BERJALAN).",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryContainer
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Lihat Detail Pesanan",
                    onClick = {
                        showSuccessDialog = false
                        onNavigateToDetail(transaksi.id)
                    },
                    variant = AppButtonVariant.CONVERSION_CTA
                )
            }
        )
    }
}

@Composable
private fun PulsingCircularProgress() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(PrimaryFixed)
        )
        CircularProgressIndicator(
            color = PrimaryContainer,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentContentPreview() {
    ManageventTheme {
        PaymentContent(
            detailState = UiState.Success(
                TransaksiModel(
                    id = "trx-1",
                    jumlahTiket = 2,
                    totalHarga = 300000.0,
                    status = "MENUNGGU_PEMBAYARAN",
                    statusPembayaran = "PENDING",
                    metodePembayaran = "bca_va",
                    nomorVirtualAccount = "88012345678901",
                    kodeReferensi = "REF-AB12CD34",
                    paymentExpiresAt = Instant.now().plusSeconds(3600).toString()
                )
            ),
            paymentState = UiState.Idle,
            paymentLoadingMessage = "Menghubungkan ke sistem pembayaran...",
            onBackClick = {},
            onPayClick = {},
            onRenewClick = {},
            onCancelClick = {},
            onNavigateToDetail = {}
        )
    }
}
