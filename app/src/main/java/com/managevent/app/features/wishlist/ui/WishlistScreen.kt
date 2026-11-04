package com.managevent.app.features.wishlist.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.wishlist.viewmodel.WishlistViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WishlistScreen(
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = viewModel(),
    onEventClick: (String) -> Unit = {}
) {
    val wishlistState by viewModel.wishlistState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchWishlist()
    }

    WishlistContent(
        wishlistState = wishlistState,
        selectedCategory = viewModel.selectedCategory,
        categories = viewModel.categories,
        onCategorySelect = viewModel::onCategorySelected,
        onEventClick = onEventClick,
        onFavoriteToggle = { eventId -> viewModel.removeFromWishlist(eventId) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistContent(
    wishlistState: UiState<List<EventModel>>,
    selectedCategory: String,
    categories: List<String>,
    onCategorySelect: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFCF8FF),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "MANAGEVENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3525CD),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Favorit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF181445),
                                fontSize = 18.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = Color(0xFF181445)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2DFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFCF8FF))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Row (Favorit Saya + Count Badge + Filter Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Favorit Saya",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF181445),
                            fontSize = 20.sp
                        )
                    )

                    val count = if (wishlistState is UiState.Success) wishlistState.data.size else 6
                    Surface(
                        color = Color(0xFFE2DFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$count Acara",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF3525CD),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF0EBFF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable {}
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color(0xFF181445),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Filter",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF181445),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Category Filter Chips (LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val isSelected = selectedCategory == category
                    val labelText = if (category == "Semua") {
                        val count = if (wishlistState is UiState.Success) wishlistState.data.size else 6
                        "Semua ($count)"
                    } else category

                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE2DFFF),
                            selectedLabelColor = Color(0xFF3525CD),
                            containerColor = Color(0xFFEFEBFF),
                            labelColor = Color(0xFF464555)
                        ),
                        border = null
                    )
                }
            }

            // Wishlist Grid View (LazyVerticalGrid 2 Columns)
            when (wishlistState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3525CD))
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = wishlistState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                is UiState.Success -> {
                    val events = wishlistState.data
                    if (events.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada event di favorit Anda.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF464555))
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 12.dp)
                        ) {
                            items(events, key = { it.id }) { event ->
                                WishlistEventCard(
                                    event = event,
                                    onEventClick = onEventClick,
                                    onFavoriteToggle = onFavoriteToggle
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun WishlistEventCard(
    event: EventModel,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    val formattedPrice = try {
        val formatter = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"))
        "Rp " + formatter.format(event.price.toLong())
    } catch (_: Exception) {
        "Rp ${event.price.toLong()}"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Box with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = if (!event.imageUrl.isNullOrEmpty()) event.imageUrl else "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=600",
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Pill Top-Left
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = event.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF181445),
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Heart Favorite Button Top-Right
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable { onFavoriteToggle(event.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Hapus dari Favorit",
                        tint = Color(0xFFFF385C),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Card Body Info
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Date Text
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFC6955),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )

                // Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF181445),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Lokasi",
                        tint = Color(0xFF777587),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF464555),
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Price
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3525CD),
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistContentPreview() {
    ManageventTheme {
        WishlistContent(
            wishlistState = UiState.Success(
                listOf(
                    EventModel(
                        id = "1",
                        title = "Nusantara Symphony Fest",
                        category = "Konser",
                        date = "14 Mei 2026",
                        location = "GBK, Jakarta",
                        price = 250000.0,
                        isFavorite = true
                    ),
                    EventModel(
                        id = "2",
                        title = "Gelaran Nikah Adat Nusantara",
                        category = "Pernikahan",
                        date = "22 Agu 2026",
                        location = "JCC Senayan",
                        price = 75000.0,
                        isFavorite = true
                    )
                )
            ),
            selectedCategory = "Semua",
            categories = listOf("Semua", "Konser Musik", "Pernikahan", "Festival", "Pameran"),
            onCategorySelect = {},
            onEventClick = {},
            onFavoriteToggle = {}
        )
    }
}
