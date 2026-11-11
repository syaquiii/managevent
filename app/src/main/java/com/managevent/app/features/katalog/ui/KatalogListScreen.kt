package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.ui.components.CategoryChipsRow
import com.managevent.app.features.katalog.ui.components.EventCard
import com.managevent.app.features.katalog.ui.components.KatalogHeader
import com.managevent.app.features.katalog.ui.components.KatalogSearchBar
import com.managevent.app.features.katalog.ui.components.LocationAndBadgeBar
import com.managevent.app.features.katalog.ui.components.SpecialPromoBanner

@Composable
fun KatalogListScreen(
    eventsState: UiState<List<EventModel>>,
    searchQuery: String,
    selectedCategory: String,
    modifier: Modifier = Modifier,
    userRole: String = "PEMESAN",
    onEventClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    KatalogListContent(
        eventsState = eventsState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        userRole = userRole,
        onEventClick = onEventClick,
        onFavoriteToggle = onFavoriteToggle,
        onSearchQueryChange = onSearchQueryChange,
        onCategorySelected = onCategorySelected,
        onAddEventClick = onAddEventClick,
        modifier = modifier
    )
}

@Composable
fun KatalogListContent(
    eventsState: UiState<List<EventModel>>,
    searchQuery: String,
    selectedCategory: String,
    userRole: String,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFBFD),
        floatingActionButton = {
            if (userRole == "VENDOR") {
                FloatingActionButton(
                    onClick = onAddEventClick,
                    containerColor = Color(0xFF3F51B5),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Event"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (eventsState) {
                is UiState.Loading -> {
                    LoadingScreen()
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = eventsState.message,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
                is UiState.Success -> {
                    val events = eventsState.data

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            Column(modifier = Modifier.padding(horizontal = 0.dp)) {
                                KatalogHeader()
                                LocationAndBadgeBar()
                                KatalogSearchBar(
                                    query = searchQuery,
                                    onQueryChange = onSearchQueryChange
                                )
                                CategoryChipsRow(
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = onCategorySelected
                                )
                                SpecialPromoBanner()

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Event Mendatang",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = Color(0xFF1C1B1F)
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE55335))
                                        )
                                    }

                                    Text(
                                        text = "${events.size} Event",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3F51B5),
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (events.isEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tidak ada event ditemukan",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF757575))
                                    )
                                }
                            }
                        }

                        items(
                            items = events,
                            key = { it.id }
                        ) { event ->
                            EventCard(
                                event = event,
                                onClick = { onEventClick(event.id) },
                                onFavoriteToggle = { onFavoriteToggle(event.id) }
                            )
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
fun KatalogListContentPreview() {
    ManageventTheme {
        KatalogListContent(
            eventsState = UiState.Success(
                listOf(
                    EventModel(
                        id = "1",
                        title = "Pameran Pernikahan Nusantara 2026",
                        category = "Pernikahan",
                        date = "18 Juli 2026",
                        location = "Jakarta Convention Center",
                        price = 150000.0,
                        imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800"
                    )
                )
            ),
            searchQuery = "",
            selectedCategory = "Semua",
            userRole = "PEMESAN",
            onEventClick = {},
            onFavoriteToggle = {},
            onSearchQueryChange = {},
            onCategorySelected = {},
            onAddEventClick = {}
        )
    }
}
