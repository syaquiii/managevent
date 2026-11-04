package com.managevent.app.features.katalog.ui

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun KatalogDetailScreen(
    eventId: String,
    modifier: Modifier = Modifier,
    viewModel: KatalogViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onBookClick: (String) -> Unit = {}
) {
    val detailState by viewModel.eventDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        if (eventId.isNotBlank()) {
            viewModel.fetchEventById(eventId)
        }
    }

    KatalogDetailContent(
        detailState = detailState,
        onBackClick = onBackClick,
        onFavoriteToggle = {
            viewModel.toggleFavorite(eventId)
            viewModel.fetchEventById(eventId)
        },
        onBookClick = onBookClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogDetailContent(
    detailState: UiState<EventModel>,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFBFD),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Event & Paket",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (detailState is UiState.Success) {
                val event = detailState.data
                val formattedPrice = try {
                    val formatter = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"))
                    "Rp " + formatter.format(event.price.toLong())
                } catch (_: Exception) {
                    "Rp ${event.price.toLong()}"
                }

                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Harga Mulai",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF757575))
                            )
                            Text(
                                text = formattedPrice,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE55335),
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Button(
                            onClick = { onBookClick(event.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Pesan Tiket Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (detailState) {
                is UiState.Loading -> {
                    LoadingScreen()
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = detailState.message,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
                is UiState.Success -> {
                    val event = detailState.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Poster Image Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            AsyncImage(
                                model = if (!event.imageUrl.isNullOrEmpty()) event.imageUrl else "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Heart Wishlist Toggle Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = onFavoriteToggle) {
                                    Icon(
                                        imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorit",
                                        tint = if (event.isFavorite) Color(0xFFFF385C) else Color(0xFF616161)
                                    )
                                }
                            }

                            // Category Pill Overlay
                            if (event.category.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF3F51B5))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = event.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Event Summary Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F)
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = "Tanggal",
                                    tint = Color(0xFF3F51B5),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = event.date,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF424242),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Place,
                                    contentDescription = "Lokasi",
                                    tint = Color(0xFFE55335),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = event.location,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF424242),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(4.dp))

                            // Description Section
                            Text(
                                text = "Deskripsi Event & Paket",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F)
                                )
                            )

                            Text(
                                text = if (event.description.isNotBlank()) event.description else "Nikmati pengalaman acara terbaik yang dikemas secara profesional oleh tim berpengalaman kami.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF616161),
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Features Card Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F8))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Fasilitas & Layanan Termasuk:",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1C1B1F)
                                        )
                                    )

                                    val facilities = listOf(
                                        "Tiket Masuk Resmi & E-Voucher",
                                        "Akses Tempat Duduk / Area Utama",
                                        "Konsultasi Vendor & Koordinasi Acara",
                                        "Jaminan Keamanan & Pelayanan 24/7"
                                    )

                                    facilities.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Check",
                                                tint = Color(0xFF3F51B5),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = item,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF424242),
                                                    fontSize = 13.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatalogDetailContentPreview() {
    ManageventTheme {
        KatalogDetailContent(
            detailState = UiState.Success(
                EventModel(
                    id = "evt-1",
                    title = "Pameran Pernikahan Nusantara 2026",
                    description = "Pameran vendor pernikahan terbesar di Nusantara dengan promo eksklusif gaun, catering, dekorasi, dan dokumentasi terlengkap.",
                    category = "Pernikahan",
                    date = "18 Juli 2026",
                    location = "Jakarta Convention Center",
                    price = 150000.0,
                    imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                    isFavorite = true
                )
            ),
            onBackClick = {},
            onFavoriteToggle = {},
            onBookClick = {}
        )
    }
}
