package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.ulasan.data.UlasanModel

val CoralColor = Color(0xFFFC6955)
val LilacActiveColor = Color(0xFFE2DFFF)
val IndigoTextColor = Color(0xFF3525CD)
val GrayInactiveColor = Color(0xFFEFEBFF)
val GrayTextColor = Color(0xFF464555)
val BackgroundColor = Color(0xFFFCF8FF)

@Composable
fun UlasanListScreen(
    uiState: UiState<List<UlasanModel>>,
    selectedFilter: Int,
    modifier: Modifier = Modifier,
    onFilterClick: (Int) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onAddUlasanClick: () -> Unit = {}
) {
    UlasanListContent(
        uiState = uiState,
        selectedFilter = selectedFilter,
        onFilterClick = onFilterClick,
        onDeleteClick = onDeleteClick,
        onAddUlasanClick = onAddUlasanClick,
        modifier = modifier
    )
}

@Composable
fun UlasanListContent(
    uiState: UiState<List<UlasanModel>>,
    selectedFilter: Int,
    onFilterClick: (Int) -> Unit,
    onDeleteClick: (String) -> Unit,
    onAddUlasanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Ulasan List") },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundColor)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onAddUlasanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralColor)
                ) {
                    Text("Tulis Ulasan", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeroRatingCard()
            }

            item {
                Text("Filter Rating:", fontWeight = FontWeight.Bold, color = Color(0xFF181445))
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
                                    color = if (isSelected) IndigoTextColor else GrayTextColor
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LilacActiveColor,
                                containerColor = GrayInactiveColor
                            ),
                            border = null
                        )
                    }
                }
            }

            when (uiState) {
                is UiState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = IndigoTextColor)
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    if (uiState.data.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada ulasan.", color = GrayTextColor)
                            }
                        }
                    } else {
                        items(uiState.data, key = { it.id }) { ulasan ->
                            UlasanItemCard(ulasan = ulasan, onDeleteClick = onDeleteClick)
                        }
                    }
                }
                else -> {}
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun HeroRatingCard() {
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
                    Text("Skor Rata-rata", color = GrayTextColor, style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("4.8", fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold, color = Color(0xFF181445))
                        Text(" / 5.0", color = GrayTextColor, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        repeat(5) { index ->
                            Icon(Icons.Default.Star, contentDescription = null, tint = if(index < 4) CoralColor else Color.LightGray, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text("Berdasarkan 124 ulasan", color = GrayTextColor, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            RatingProgressBar(star = 5, count = 96, percentage = 0.78f)
            RatingProgressBar(star = 4, count = 18, percentage = 0.15f)
            RatingProgressBar(star = 3, count = 6, percentage = 0.05f)
            RatingProgressBar(star = 2, count = 3, percentage = 0.02f)
            RatingProgressBar(star = 1, count = 1, percentage = 0.01f)
        }
    }
}

@Composable
fun RatingProgressBar(star: Int, count: Int, percentage: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(star.toString(), style = MaterialTheme.typography.labelSmall, color = GrayTextColor, modifier = Modifier.width(16.dp))
        Icon(Icons.Default.Star, contentDescription = null, tint = CoralColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = CoralColor,
            trackColor = GrayInactiveColor,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = GrayTextColor, modifier = Modifier.width(24.dp))
    }
}

@Composable
fun UlasanItemCard(
    ulasan: UlasanModel,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(LilacActiveColor, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(ulasan.username, fontWeight = FontWeight.Bold, color = Color(0xFF181445), style = MaterialTheme.typography.titleSmall)
                        Text("18 Juli 2026", color = GrayTextColor, style = MaterialTheme.typography.bodySmall)
                    }
                }
                IconButton(onClick = { onDeleteClick(ulasan.id) }) {
                    Icon(Icons.Default.Delete, "Hapus", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                repeat(5) { i ->
                    Icon(Icons.Default.Star, contentDescription = null, tint = if (i < ulasan.rating) CoralColor else Color.LightGray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(ulasan.comment, color = Color(0xFF181445), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UlasanListContentPreview() {
    ManageventTheme {
        UlasanListContent(
            uiState = UiState.Success(listOf(
                UlasanModel(id = "1", username = "Sarah Wijaya", rating = 5, comment = "Pengalaman pertama kali hadir di festival musik ini dan luar biasa rapi."),
                UlasanModel(id = "2", username = "Budi Pratama", rating = 4, comment = "Bintang tamu dan visual panggung spektakuler!")
            )),
            selectedFilter = 0,
            onFilterClick = {},
            onDeleteClick = {},
            onAddUlasanClick = {}
        )
    }
}
