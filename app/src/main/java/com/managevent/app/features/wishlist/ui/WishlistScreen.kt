package com.managevent.app.features.wishlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun WishlistScreen(
    modifier: Modifier = Modifier
) {
    WishlistContent(
        modifier = modifier
    )
}

@Composable
fun WishlistContent(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Daftar Wishlist") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Event Favorit Anda")
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = listOf("1", "2", "3", "4"),
                    key = { it }
                ) { index ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Wishlist Event #$index")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistContentPreview() {
    ManageventTheme {
        WishlistContent()
    }
}
