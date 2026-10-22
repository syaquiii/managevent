package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar

@Composable
fun KatalogListScreen(
    onEventClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { AppTopBar(title = "Katalog Event") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEventClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Event")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Daftar Katalog Event")
            // Placeholder LazyColumn
            LazyColumn {
                items(5) { index ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = "Event Item #${index + 1}")
                    }
                }
            }
        }
    }
}
