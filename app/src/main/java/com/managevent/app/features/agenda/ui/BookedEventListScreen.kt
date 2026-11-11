package com.managevent.app.features.agenda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState
import com.managevent.app.features.agenda.data.BookedEventModel

@Composable
fun BookedEventListScreen(
    state: UiState<List<BookedEventModel>>,
    query: String,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onOpenNotes: (String) -> Unit = {}
) {
    BookedEventListContent(
        state = state,
        query = query,
        onQueryChange = onQueryChange,
        onOpenNotes = onOpenNotes,
        modifier = modifier
    )
}

@Composable
fun BookedEventListContent(
    state: UiState<List<BookedEventModel>>,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenNotes: (String) -> Unit,
    modifier: Modifier = Modifier,
    onExplore: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Agenda Persiapan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cari event dipesan") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            when (state) {
                is UiState.Idle, is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    val items = state.data.filter {
                        it.title.contains(query, ignoreCase = true)
                    }
                    if (items.isEmpty()) {
                        EmptyBookedState(isSearching = query.isNotBlank(), onExplore = onExplore)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(items, key = { it.eventId }) { event ->
                                BookedEventCard(event = event, onOpenNotes = { onOpenNotes(event.eventId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookedEventCard(
    event: BookedEventModel,
    onOpenNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (!event.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF3525CD),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = event.date ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (!event.location.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFFC6955),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        StatusChip(status = event.status)

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFE2DFFF))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                    contentDescription = null,
                                    tint = Color(0xFF3525CD),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${event.noteCount} Agenda",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3525CD)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onOpenNotes,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE2DFFF),
                    contentColor = Color(0xFF3525CD)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Buka Agenda",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val done = status.equals("Selesai", ignoreCase = true)
    val container = if (done) MaterialTheme.colorScheme.surfaceContainerHigh else Color(0xFFE6F4EA)
    val content = if (done) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF005338)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(content)
            )
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = content
            )
        }
    }
}

@Composable
private fun EmptyBookedState(isSearching: Boolean, onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isSearching) "Event tidak ditemukan" else "Belum ada event dipesan",
            style = MaterialTheme.typography.titleMedium
        )
        if (!isSearching) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onExplore,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3525CD))
            ) {
                Text("Jelajahi Event")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookedEventListContentPreview() {
    ManageventTheme {
        BookedEventListContent(
            state = UiState.Success(
                listOf(
                    BookedEventModel("1", "Pameran Pernikahan Nusantara 2026", "18 Juli 2026", "Grand Ballroom Jakarta", null, "Berjalan", 6)
                )
            ),
            query = "",
            onQueryChange = {},
            onOpenNotes = {}
        )
    }
}
