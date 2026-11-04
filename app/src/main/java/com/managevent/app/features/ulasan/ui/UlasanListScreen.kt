package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Daftar Ulasan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Ulasan Pengguna")
            LazyColumn {
                items(
                    items = listOf("1", "2", "3"),
                    key = { it }
                ) { index ->
                    Text(
                        text = "User #$index: Event sangat bagus! ⭐⭐⭐⭐⭐",
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
