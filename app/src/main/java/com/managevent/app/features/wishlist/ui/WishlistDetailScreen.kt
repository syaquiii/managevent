package com.managevent.app.features.wishlist.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SurfaceContainer
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.formatRupiah
import com.managevent.app.features.katalog.data.EventModel

@Composable
fun WishlistDetailScreen(
    detailState: UiState<EventModel>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onRemoveFromWishlist: (String) -> Unit = {},
    onBookClick: (String) -> Unit = {}
) {
    WishlistDetailContent(
        detailState = detailState,
        onBackClick = onBackClick,
        onRemoveFromWishlist = onRemoveFromWishlist,
        onBookClick = onBookClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailContent(
    detailState: UiState<EventModel>,
    onBackClick: () -> Unit,
    onRemoveFromWishlist: (String) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Favorit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                actions = {
                    if (detailState is UiState.Success) {
                        IconButton(onClick = { onRemoveFromWishlist(detailState.data.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus dari Favorit",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (detailState is UiState.Success) {
                val event = detailState.data
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppButton(
                            text = "Pesan Sekarang →",
                            onClick = { onBookClick(event.id) },
                            variant = AppButtonVariant.CONVERSION_CTA
                        )
                        AppButton(
                            text = "Hapus dari Favorit",
                            onClick = { onRemoveFromWishlist(event.id) },
                            variant = AppButtonVariant.SECONDARY_OUTLINED
                        )
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
                is UiState.Idle, is UiState.Loading -> LoadingScreen()
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = detailState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            AsyncImage(
                                model = if (!event.imageUrl.isNullOrEmpty()) event.imageUrl else "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (event.category.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ActiveFilterChipBackground)
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = event.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryContainer
                                        )
                                    )
                                }
                            }
                        }

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
                                    color = OnBackground
                                )
                            )

                            Text(
                                text = formatRupiah(event.price),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryContainer
                                )
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CalendarToday,
                                            contentDescription = null,
                                            tint = PrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text("Tanggal Pelaksanaan", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                            Text(event.date.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Place,
                                            contentDescription = null,
                                            tint = PrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text("Lokasi Acara", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                            Text(event.location.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Deskripsi Event",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnBackground
                            )
                            Text(
                                text = event.description.ifBlank { "Deskripsi belum tersedia." },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = OnSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistDetailContentPreview() {
    ManageventTheme {
        WishlistDetailContent(
            detailState = UiState.Success(
                EventModel(
                    id = "1",
                    title = "Nusantara Symphony Fest",
                    description = "Konser musik simfoni megah menyajikan karya maestro Nusantara.",
                    category = "Konser",
                    date = "14 Mei 2026",
                    location = "Gelora Bung Karno, Jakarta",
                    price = 250000.0,
                    isFavorite = true
                )
            ),
            onBackClick = {},
            onRemoveFromWishlist = {},
            onBookClick = {}
        )
    }
}
