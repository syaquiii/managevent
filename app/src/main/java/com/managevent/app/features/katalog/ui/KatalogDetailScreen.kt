package com.managevent.app.features.katalog.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
    onBookClick: (String) -> Unit = {},
    onReviewClick: (String) -> Unit = {}
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
        onReviewClick = onReviewClick,
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
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFBFD),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Vendor / Event",
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
                        // Wishlist Heart Icon Button
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3F4F8))
                        ) {
                            Icon(
                                imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (event.isFavorite) Color(0xFFFF385C) else Color(0xFF616161)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { onBookClick(event.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC6955)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Pesan Sekarang ->",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
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

                            // Status Pill
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔴 Pendaftaran Dibuka",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                )
                            }

                            // Category Pill Overlay
                            if (event.category.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE2DFFF))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = event.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3525CD),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Event Title & Rating Section
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
                                    color = Color(0xFF181445),
                                    fontSize = 20.sp
                                )
                            )

                            // Rating Row -> Clickable to navigate to UlasanListScreen!
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onReviewClick(event.id) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFC6955),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "4.8",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3525CD)
                                    )
                                )
                                Text(
                                    text = "(124 Ulasan)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF464555)
                                    )
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Buka Halaman Ulasan",
                                    tint = Color(0xFF3525CD),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Event Info Card (Date & Location)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EBFF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2DFFF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CalendarToday,
                                                contentDescription = "Tanggal",
                                                tint = Color(0xFF3525CD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text("Tanggal Pelaksanaan", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF464555)))
                                            Text(event.date, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF181445)))
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2DFFF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Place,
                                                contentDescription = "Lokasi",
                                                tint = Color(0xFF3525CD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text("Lokasi Acara", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF464555)))
                                            Text(event.location, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF181445)))
                                        }
                                    }
                                }
                            }

                            // Description Section
                            Text(
                                text = "Deskripsi Event",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF181445)
                                )
                            )

                            Text(
                                text = if (event.description.isNotBlank()) event.description else "Pameran vendor pernikahan terlengkap di Indonesia menghadirkan lebih dari 200 vendor terkurasi mulai dari dekorasi, katering, tata rias, busana pengantin, hingga dokumentasi foto dan video profesional.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF464555),
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Features Badges Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F2FF))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF3525CD), modifier = Modifier.size(18.dp))
                                        Text("200+ Vendor Resmi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF181445)))
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
                    description = "Pameran vendor pernikahan terlengkap di Indonesia menghadirkan lebih dari 200 vendor terkurasi.",
                    category = "Pernikahan",
                    date = "18 - 20 Juli 2026",
                    location = "Jakarta Convention Center (JCC), Senayan, Jakarta Pusat",
                    price = 150000.0,
                    imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                    isFavorite = true
                )
            ),
            onBackClick = {},
            onFavoriteToggle = {},
            onBookClick = {},
            onReviewClick = {}
        )
    }
}
