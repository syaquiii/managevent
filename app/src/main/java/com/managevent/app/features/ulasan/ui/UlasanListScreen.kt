package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun UlasanListScreen(
    modifier: Modifier = Modifier
) {
    UlasanListContent(
        modifier = modifier
    )
}

@Composable
fun UlasanListContent(
    modifier: Modifier = Modifier
) {
    var selectedRatingFilter by remember { mutableIntStateOf(0) } // 0 = Semua

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Daftar Ulasan & Rating") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Filter Rating:")
            
            // LazyRow untuk Filter Bintang Rating
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(
                    items = listOf(0, 5, 4, 3, 2, 1),
                    key = { it }
                ) { rating ->
                    FilterChip(
                        selected = selectedRatingFilter == rating,
                        onClick = { selectedRatingFilter = rating },
                        label = { Text(if (rating == 0) "Semua" else "$rating ⭐") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // LazyColumn untuk Daftar Ulasan
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(
                    items = listOf("1", "2", "3"),
                    key = { it }
                ) { index ->
                    Text(
                        text = "User #$index: Event sangat memuaskan! ⭐⭐⭐⭐⭐",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UlasanListContentPreview() {
    ManageventTheme {
        UlasanListContent()
    }
}
