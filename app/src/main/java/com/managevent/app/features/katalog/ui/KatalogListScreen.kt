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
    onEventClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()

    KatalogListContent(
        eventsState = eventsState,
        onEventClick = onEventClick,
        onAddEventClick = onAddEventClick,
        modifier = modifier
    )
}

@Composable
fun KatalogListContent(
    eventsState: UiState<List<EventModel>>,
    onEventClick: (String) -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
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
            LazyColumn {
                items(
                    items = listOf("1", "2", "3", "4", "5"),
                    key = { it }
                ) { index ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = "Event Item #$index")
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
                listOf(EventModel(id = "1", title = "Konser Musik"))
            ),
            onEventClick = {},
            onAddEventClick = {}
        )
    }
}
