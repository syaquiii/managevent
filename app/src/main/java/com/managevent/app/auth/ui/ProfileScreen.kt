package com.managevent.app.auth.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun ProfileScreen(
    user: UserModel = UserModel(
        email = "user@managevent.com",
        fullName = "Sarah Wijaya",
        role = "PEMESAN"
    ),
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {
    ProfileContent(
        user = user,
        onLogoutClick = onLogoutClick,
        modifier = modifier
    )
}

@Composable
fun ProfileContent(
    user: UserModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppTopBar(title = "Profil Pengguna") },
        containerColor = Color(0xFFFCF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Image with Edit Badge
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFE2DFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 42.sp)
                }

                // Edit Camera Icon Overlay
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3525CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah Foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = if (!user.fullName.isNullOrBlank()) user.fullName else "Nama Pengguna",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF181445),
                    fontSize = 20.sp
                )
            )

            // Email
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF464555))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role Badge Pill
            Surface(
                color = Color(0xFFE2DFFF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (user.role == "VENDOR") "🎪 VENDOR / EO" else "🎫 PEMESAN TIKET",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF3525CD),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Profile Options Menu Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ProfileOptionItem(
                        icon = Icons.Default.AccountCircle,
                        title = "Ubah Profil & Data Diri",
                        onClick = {}
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Receipt,
                        title = "Riwayat Transaksi Saya",
                        onClick = {}
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Favorite,
                        title = "Koleksi Favorit Saya",
                        onClick = {}
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Lock,
                        title = "Keamanan & Kata Sandi",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            AppButton(
                text = "Keluar dari Akun (Logout)",
                onClick = onLogoutClick,
                variant = AppButtonVariant.SECONDARY_OUTLINED
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF6F2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF3525CD),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181445)
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Buka",
            tint = Color(0xFF777587),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    ManageventTheme {
        ProfileContent(
            user = UserModel(
                email = "sarah.wijaya@example.com",
                fullName = "Sarah Wijaya",
                role = "PEMESAN"
            ),
            onLogoutClick = {}
        )
    }
}
