package com.managevent.app.features.wishlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar

@Composable
fun WishlistDetailScreen(
    eventId: String = ""
) {
    Scaffold(
        topBar = { AppTopBar(title = "Detail Event Wishlist") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Detail Event Wishlist ID: $eventId")
        }
    }
}
