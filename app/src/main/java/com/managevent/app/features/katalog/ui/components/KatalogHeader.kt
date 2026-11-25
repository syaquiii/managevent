package com.managevent.app.features.katalog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
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
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.PrimaryContainer

@Composable
fun KatalogHeader(
    modifier: Modifier = Modifier,
    userName: String = "",
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = "Managevent Logo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "MANAGEVENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = OnSurfaceVariant
                    )
                )
                Text(
                    text = if (userName.isNotBlank()) "Halo, ${userName.substringBefore(" ")}" else "Beranda",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = OnBackground
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ActiveFilterChipBackground)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            if (userName.isNotBlank()) {
                Text(
                    text = userName.trim().first().uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    tint = PrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KatalogHeaderPreview() {
    ManageventTheme {
        KatalogHeader(userName = "Sarah Wijaya")
    }
}
