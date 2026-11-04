package com.managevent.app.features.agenda.ui

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
fun AgendaListScreen(
    modifier: Modifier = Modifier
) {
    AgendaListContent(
        modifier = modifier
    )
}

@Composable
fun AgendaListContent(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Daftar Agenda") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Agenda Saya Per Tanggal")
            LazyColumn {
                items(
                    items = listOf("1", "2", "3"),
                    key = { it }
                ) { index ->
                    Text(
                        text = "Agenda #$index - 22 Oktober 2026",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendaListContentPreview() {
    ManageventTheme {
        AgendaListContent()
    }
}
