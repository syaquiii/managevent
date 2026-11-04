package com.managevent.app.features.katalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.features.katalog.data.EventModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EventCard(
    event: EventModel,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedPrice = try {
        val formatter = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"))
        "Rp " + formatter.format(event.price.toLong())
    } catch (_: Exception) {
        "Rp ${event.price.toLong()}"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Poster Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = if (!event.imageUrl.isNullOrEmpty()) event.imageUrl else "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800",
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Heart Wishlist Button (Top-Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (event.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (event.isFavorite) Color(0xFFFF385C) else Color(0xFF616161),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Category Badge (Bottom-Left)
                if (event.category.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = event.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Details Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1F1F1F)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Date Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Tanggal",
                        tint = Color(0xFF3F51B5),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF616161),
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Location Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "Lokasi",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF757575),
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(2.dp))

                // Price Section
                Text(
                    text = "Mulai dari",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF757575),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE55335),
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    ManageventTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EventCard(
                event = EventModel(
                    id = "1",
                    title = "Pameran Pernikahan Nusantara 2026",
                    category = "Pernikahan",
                    date = "18 Juli 2026",
                    location = "Jakarta Convention Center",
                    price = 150000.0,
                    imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800"
                ),
                onClick = {},
                onFavoriteToggle = {}
            )
        }
    }
}
