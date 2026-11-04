package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun KatalogDetailScreen(
    eventId: String = "",
    modifier: Modifier = Modifier
) {
    KatalogDetailContent(
        eventId = eventId,
        modifier = modifier
    )
}

@Composable
fun KatalogDetailContent(
    eventId: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Detail Event") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Detail Event ID: $eventId")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Deskripsi event akan ditampilkan di sini.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatalogDetailContentPreview() {
    ManageventTheme {
        KatalogDetailContent(eventId = "EVT-001")
    }
}
