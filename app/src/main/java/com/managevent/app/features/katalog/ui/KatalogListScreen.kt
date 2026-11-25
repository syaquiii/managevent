package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.LoadingScreen
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SecondaryContainer
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
import com.managevent.app.features.katalog.ui.components.CategoryChipsRow
import com.managevent.app.features.katalog.ui.components.EventCard
import com.managevent.app.features.katalog.ui.components.KatalogHeader
import com.managevent.app.features.katalog.ui.components.KatalogSearchBar
import com.managevent.app.features.katalog.ui.components.LocationAndBadgeBar
import com.managevent.app.features.katalog.ui.components.SpecialPromoBanner
import com.managevent.app.features.katalog.viewmodel.KatalogSortOption

@Composable
fun KatalogListScreen(
    eventsState: UiState<List<EventModel>>,
    searchQuery: String,
    selectedCategory: String,
    modifier: Modifier = Modifier,
    selectedLocation: String = "Semua Lokasi",
    availableLocations: List<String> = listOf("Semua Lokasi"),
    sortOption: KatalogSortOption = KatalogSortOption.TERBARU,
    userRole: String = "PEMESAN",
    userName: String = "",
    onEventClick: (String) -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onLocationSelected: (String) -> Unit = {},
    onSortOptionSelected: (KatalogSortOption) -> Unit = {},
    onResetFilters: () -> Unit = {},
    onRetry: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    KatalogListContent(
        eventsState = eventsState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        selectedLocation = selectedLocation,
        availableLocations = availableLocations,
        sortOption = sortOption,
        userRole = userRole,
        userName = userName,
        onEventClick = onEventClick,
        onFavoriteToggle = onFavoriteToggle,
        onSearchQueryChange = onSearchQueryChange,
        onCategorySelected = onCategorySelected,
        onLocationSelected = onLocationSelected,
        onSortOptionSelected = onSortOptionSelected,
        onResetFilters = onResetFilters,
        onRetry = onRetry,
        onProfileClick = onProfileClick,
        onAddEventClick = onAddEventClick,
        modifier = modifier
    )
}

@Composable
fun KatalogListContent(
    eventsState: UiState<List<EventModel>>,
    searchQuery: String,
    selectedCategory: String,
    selectedLocation: String,
    availableLocations: List<String>,
    sortOption: KatalogSortOption,
    userRole: String,
    userName: String,
    onEventClick: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onLocationSelected: (String) -> Unit,
    onSortOptionSelected: (KatalogSortOption) -> Unit,
    onResetFilters: () -> Unit,
    onRetry: () -> Unit,
    onProfileClick: () -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (userRole == "VENDOR") {
                FloatingActionButton(
                    onClick = onAddEventClick,
                    shape = RoundedCornerShape(16.dp),
                    containerColor = SecondaryContainer,
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
                is UiState.Idle, is UiState.Loading -> {
                    LoadingScreen()
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = eventsState.message,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                        )
                        AppButton(
                            text = "Muat Ulang",
                            onClick = onRetry,
                            variant = AppButtonVariant.SECONDARY_OUTLINED,
                            modifier = Modifier.padding(top = 16.dp)
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
                            Column {
                                KatalogHeader(
                                    userName = userName,
                                    onProfileClick = onProfileClick
                                )
                                LocationAndBadgeBar(
                                    currentLocation = selectedLocation,
                                    onLocationClick = { showLocationDialog = true }
                                )
                                KatalogSearchBar(
                                    query = searchQuery,
                                    onQueryChange = onSearchQueryChange,
                                    onFilterClick = { showFilterDialog = true }
                                )
                                CategoryChipsRow(
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = onCategorySelected
                                )
                                SpecialPromoBanner()

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 12.dp),
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
                                                color = OnBackground
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(SecondaryContainer)
                                        )
                                    }

                                    Text(
                                        text = "${events.size} Event",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryContainer,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (events.isEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Tidak ada event ditemukan",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                                    )
                                    if (searchQuery.isNotBlank() || selectedCategory != "Semua" || selectedLocation != "Semua Lokasi") {
                                        TextButton(onClick = onResetFilters) {
                                            Text("Reset Filter", color = PrimaryContainer, fontWeight = FontWeight.Bold)
                                        }
                                    }
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
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Pilih Lokasi", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    availableLocations.forEach { loc ->
                        val isSelected = loc == selectedLocation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onLocationSelected(loc)
                                    showLocationDialog = false
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onLocationSelected(loc)
                                    showLocationDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryContainer)
                            )
                            Text(text = loc, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Tutup")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Urutkan & Filter", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Urutkan Berdasarkan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    KatalogSortOption.entries.forEach { option ->
                        val isSelected = option == sortOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSortOptionSelected(option)
                                    showFilterDialog = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onSortOptionSelected(option)
                                    showFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryContainer)
                            )
                            Text(text = option.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetFilters()
                        showFilterDialog = false
                    }
                ) {
                    Text("Reset Semua", color = SecondaryContainer, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Tutup")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
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
            selectedLocation = "Semua Lokasi",
            availableLocations = listOf("Semua Lokasi", "Jakarta"),
            sortOption = KatalogSortOption.TERBARU,
            userRole = "PEMESAN",
            userName = "Sarah Wijaya",
            onEventClick = {},
            onFavoriteToggle = {},
            onSearchQueryChange = {},
            onCategorySelected = {},
            onLocationSelected = {},
            onSortOptionSelected = {},
            onResetFilters = {},
            onRetry = {},
            onProfileClick = {},
            onAddEventClick = {}
        )
    }
}
