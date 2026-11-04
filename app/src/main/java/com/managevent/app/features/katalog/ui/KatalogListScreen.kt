package com.managevent.app.features.katalog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.katalog.data.EventModel
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

    KatalogListContent(
        eventsState = eventsState,
        userRole = userRole,
        onEventClick = onEventClick,
        onAddEventClick = onAddEventClick,
        modifier = modifier
    )
}

@Composable
fun KatalogListContent(
    eventsState: UiState<List<EventModel>>,
    userRole: String,
    onEventClick: (String) -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Katalog Event") },
        floatingActionButton = {
            // FAB Khusus Vendor sesuai RBAC
            if (userRole == "VENDOR") {
                FloatingActionButton(onClick = onAddEventClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Event")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Daftar Paket & Event",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tampilan Grid: LazyVerticalGrid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = listOf(
                        EventModel("1", "Pernikahan Mewah", "Paket pernikahan indoor"),
                        EventModel("2", "Seminar Nasional", "Seminar & Workshop IT"),
                        EventModel("3", "Konser Musik", "Festival musik outdoor")
                    ),
                    key = { it.id }
                ) { event ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { onEventClick(event.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = event.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
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
                listOf(EventModel(id = "1", title = "Pernikahan Mewah"))
            ),
            userRole = "VENDOR",
            onEventClick = {},
            onAddEventClick = {}
        )
    }
}
