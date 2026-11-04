package com.managevent.app.features.katalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun LocationAndBadgeBar(
    modifier: Modifier = Modifier,
    currentLocation: String = "Indonesia",
    onLocationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickable { onLocationClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.NearMe,
                contentDescription = "Lokasi Icon",
                tint = Color(0xFF3F51B5),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Lokasi: ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF616161)
                )
            )
            Text(
                text = currentLocation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Pilih Lokasi",
                tint = Color(0xFF616161),
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFEDE7F6))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Tiket Resmi",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5E35B1),
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationAndBadgeBarPreview() {
    ManageventTheme {
        LocationAndBadgeBar()
    }
}
