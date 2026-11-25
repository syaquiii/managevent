package com.managevent.app.features.ulasan.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.InactiveFilterChipBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SecondaryContainer
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.formatDateId
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.viewmodel.RatingSummary
import kotlinx.coroutines.flow.SharedFlow
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun UlasanListScreen(
    uiState: UiState<List<UlasanModel>>,
    ratingSummary: RatingSummary,
    selectedFilter: Int,
    currentUserId: String,
    onFilterClick: (Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (UlasanModel) -> Unit,
    onAddUlasanClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionMessage: SharedFlow<String>? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionMessage) {
        actionMessage?.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    UlasanListContent(
        uiState = uiState,
        ratingSummary = ratingSummary,
        selectedFilter = selectedFilter,
        currentUserId = currentUserId,
        snackbarHostState = snackbarHostState,
        onFilterClick = onFilterClick,
        onDeleteClick = onDeleteClick,
        onEditClick = onEditClick,
        onAddUlasanClick = onAddUlasanClick,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UlasanListContent(
    uiState: UiState<List<UlasanModel>>,
    ratingSummary: RatingSummary,
    selectedFilter: Int,
    currentUserId: String,
    onFilterClick: (Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (UlasanModel) -> Unit,
    onAddUlasanClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var ulasanToDelete by remember { mutableStateOf<UlasanModel?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ulasan Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onAddUlasanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer)
                ) {
                    Text(
                        "Tulis Ulasan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                HeroRatingCard(summary = ratingSummary)
            }
            item {
                Text("Filter Rating:", fontWeight = FontWeight.Bold, color = OnBackground)
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf(0, 5, 4, 3, 2, 1), key = { it }) { rating ->
                        val isSelected = selectedFilter == rating
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterClick(rating) },
                            label = {
                                Text(
                                    text = if (rating == 0) "Semua" else "$rating Bintang",
                                    color = if (isSelected) PrimaryContainer else OnSurfaceVariant
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ActiveFilterChipBackground,
                                containerColor = InactiveFilterChipBackground
                            ),
                            border = null
                        )
                    }
                }
            }
            when (uiState) {
                is UiState.Idle, is UiState.Loading -> item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryContainer)
                    }
                }
                is UiState.Error -> item {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
                is UiState.Success -> {
                    if (uiState.data.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedFilter == 0) "Belum ada ulasan untuk event ini." else "Tidak ada ulasan dengan rating $selectedFilter bintang.",
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(uiState.data, key = { it.id }) { ulasan ->
                            UlasanItemCard(
                                ulasan = ulasan,
                                isOwner = currentUserId.isNotBlank() && ulasan.userId == currentUserId,
                                onDeleteClick = { ulasanToDelete = ulasan },
                                onEditClick = onEditClick
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    ulasanToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { ulasanToDelete = null },
            title = { Text("Hapus Ulasan?", fontWeight = FontWeight.Bold) },
            text = { Text("Ulasan Anda akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(target.id)
                        ulasanToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { ulasanToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun UlasanItemCard(
    ulasan: UlasanModel,
    isOwner: Boolean,
    onDeleteClick: (String) -> Unit,
    onEditClick: (UlasanModel) -> Unit
) {
    val displayName = ulasan.username.ifBlank { "Pengguna" }
    val formattedDate = formatDateId(ulasan.createdAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ActiveFilterChipBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.trim().first().uppercase(),
                            color = PrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            displayName,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (formattedDate == "-") "Baru saja" else formattedDate,
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (isOwner) {
                    Row {
                        IconButton(onClick = { onEditClick(ulasan) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = PrimaryContainer)
                        }
                        IconButton(onClick = { onDeleteClick(ulasan.id) }) {
                            Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(5) { i ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < ulasan.rating) SecondaryContainer else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(ulasan.comment, color = OnBackground, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun HeroRatingCard(summary: RatingSummary) {
    val avgFormatted = if (summary.totalCount > 0) {
        String.format(Locale.US, "%.1f", summary.averageRating)
    } else {
        "0.0"
    }
    val filledStars = summary.averageRating.roundToInt().coerceIn(0, 5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Skor Rata-rata", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            avgFormatted,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            color = OnBackground
                        )
                        Text(" / 5.0", color = OnSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < filledStars) SecondaryContainer else Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Berdasarkan ${summary.totalCount} ulasan",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            for (star in 5 downTo 1) {
                RatingProgressBar(
                    star = star,
                    count = summary.countsByStar[star] ?: 0,
                    percentage = summary.percentagesByStar[star] ?: 0f
                )
            }
        }
    }
}

@Composable
fun RatingProgressBar(star: Int, count: Int, percentage: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Text(star.toString(), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, modifier = Modifier.width(16.dp))
        Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SecondaryContainer,
            trackColor = InactiveFilterChipBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, modifier = Modifier.width(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun UlasanListContentPreview() {
    ManageventTheme {
        UlasanListContent(
            uiState = UiState.Success(
                listOf(
                    UlasanModel(
                        id = "u-1",
                        eventId = "e-1",
                        userId = "usr-1",
                        username = "Sarah Wijaya",
                        rating = 5,
                        comment = "Acara tertata rapi dan vendornya sangat profesional!",
                        createdAt = "2026-11-11T10:51:34+00:00"
                    )
                )
            ),
            ratingSummary = RatingSummary(
                averageRating = 5.0,
                totalCount = 1,
                countsByStar = mapOf(5 to 1, 4 to 0, 3 to 0, 2 to 0, 1 to 0),
                percentagesByStar = mapOf(5 to 1f, 4 to 0f, 3 to 0f, 2 to 0f, 1 to 0f)
            ),
            selectedFilter = 0,
            currentUserId = "usr-1",
            onFilterClick = {},
            onDeleteClick = {},
            onEditClick = {},
            onAddUlasanClick = {},
            onBackClick = {}
        )
    }
}
