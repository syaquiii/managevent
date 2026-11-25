package com.managevent.app.features.wishlist.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.InactiveFilterChipBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.Outline
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SecondaryContainer
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.formatRupiah
import com.managevent.app.features.katalog.data.EventModel

@Composable
fun WishlistScreen(
    wishlistState: UiState<List<EventModel>>,
    selectedCategory: String,
    totalCount: Int,
    categories: List<String>,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = true,
    onCategorySelect: (String) -> Unit = {},
    onEventClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    WishlistContent(
        wishlistState = wishlistState,
        selectedCategory = selectedCategory,
        totalCount = totalCount,
        categories = categories,
        isLoggedIn = isLoggedIn,
        onCategorySelect = onCategorySelect,
        onEventClick = onEventClick,
        onFavoriteToggle = onFavoriteToggle,
        onNavigateToProfile = onNavigateToProfile,
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistContent(
    wishlistState: UiState<List<EventModel>>,
    selectedCategory: String,
    totalCount: Int,
    categories: List<String>,
    isLoggedIn: Boolean,
    onCategorySelect: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                                color = PrimaryContainer,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Favorit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnBackground,
                                fontSize = 18.sp
                            )
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ActiveFilterChipBackground)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = PrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { innerPadding ->
        if (!isLoggedIn) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = PrimaryContainer,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Masuk untuk Menyimpan Favorit",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Simpan event pilihan Anda ke daftar favorit agar mudah dibandingkan dan dipesan nanti.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                AppButton(
                    text = "Masuk Akun",
                    onClick = onLoginClick,
                    variant = AppButtonVariant.CONVERSION_CTA
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
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
                            color = OnBackground,
                            fontSize = 20.sp
                        )
                    )

                    Surface(
                        color = ActiveFilterChipBackground,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$totalCount Acara",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PrimaryContainer,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val isSelected = selectedCategory == category
                    val labelText = if (category == "Semua") "Semua ($totalCount)" else category

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
                            selectedContainerColor = ActiveFilterChipBackground,
                            selectedLabelColor = PrimaryContainer,
                            containerColor = InactiveFilterChipBackground,
                            labelColor = OnSurfaceVariant
                        ),
                        border = null
                    )
                }
            }

            when (wishlistState) {
                is UiState.Idle, is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryContainer)
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
                                text = if (selectedCategory == "Semua") {
                                    "Belum ada event di daftar favorit Anda."
                                } else {
                                    "Tidak ada event favorit di kategori '$selectedCategory'."
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = OnSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
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
    val formattedPrice = formatRupiah(event.price)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
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
                            color = OnBackground,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

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

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Lokasi",
                        tint = Outline,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer,
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
                    )
                )
            ),
            selectedCategory = "Semua",
            totalCount = 1,
            categories = listOf("Semua", "Pernikahan", "Konser", "Seminar", "Ulang Tahun", "Festival", "Pameran"),
            isLoggedIn = true,
            onCategorySelect = {},
            onEventClick = {},
            onFavoriteToggle = {},
            onNavigateToProfile = {},
            onLoginClick = {}
        )
    }
}
