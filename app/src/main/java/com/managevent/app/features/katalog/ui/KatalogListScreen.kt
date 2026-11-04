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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.managevent.app.features.katalog.viewmodel.KatalogViewModel

@Composable
fun KatalogListScreen(
    modifier: Modifier = Modifier,
    viewModel: KatalogViewModel = viewModel(),
    userRole: String = "PEMESAN", // "VENDOR" atau "PEMESAN"
    onEventClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    KatalogListContent(
        eventsState = eventsState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        userRole = userRole,
        onEventClick = onEventClick,
        onFavoriteToggle = { eventId -> viewModel.toggleFavorite(eventId) },
        onSearchQueryChange = { query -> viewModel.onSearchQueryChange(query) },
        onCategorySelected = { category -> viewModel.onCategorySelected(category) },
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
                        // Section Top Headers
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

                                // Section Title: Event Mendatang
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
                                        // Dot Oranye Indicator
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

                        // Empty State if no events match search/filter
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

                        // Event Cards Grid
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
                    ),
                    EventModel(
                        id = "2",
                        title = "Jakarta Jazz Symphony 2026",
                        category = "Konser",
                        date = "22 Agustus 2026",
                        location = "Istora Senayan",
                        price = 350000.0,
                        imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800"
                    ),
                    EventModel(
                        id = "3",
                        title = "Indonesia Tech Summit 2027",
                        category = "Seminar",
                        date = "14 Februari 2027",
                        location = "ICE BSD Tangerang",
                        price = 250000.0,
                        imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?q=80&w=800"
                    ),
                    EventModel(
                        id = "4",
                        title = "Kids & Family Birthday Festival",
                        category = "Ulang Tahun",
                        date = "10 Mei 2026",
                        location = "Grand City Surabaya",
                        price = 75000.0,
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?q=80&w=800"
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
