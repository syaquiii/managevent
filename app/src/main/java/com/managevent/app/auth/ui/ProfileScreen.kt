package com.managevent.app.auth.ui

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
        fullName = "Pengguna Managevent",
        role = "PEMESAN"
    ),
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
    onNavigateToRiwayat: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onUpdateProfile: (String, String) -> Unit = { _, _ -> },
    onUpdatePassword: (String) -> Unit = {}
) {
    ProfileContent(
        user = user,
        onLogoutClick = onLogoutClick,
        onNavigateToRiwayat = onNavigateToRiwayat,
        onNavigateToWishlist = onNavigateToWishlist,
        onUpdateProfile = onUpdateProfile,
        onUpdatePassword = onUpdatePassword,
        modifier = modifier
    )
}

@Composable
fun ProfileContent(
    user: UserModel,
    onLogoutClick: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

            // Avatar Container with Vector Icon & Edit Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clickable { showEditProfileDialog = true },
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFE2DFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto Profil",
                        tint = Color(0xFF3525CD),
                        modifier = Modifier.size(48.dp)
                    )
                }

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
                text = user.email.ifEmpty { "user@managevent.com" },
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF464555))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Role Badge Pill with Vector Icon
            Surface(
                color = Color(0xFFE2DFFF),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { showEditProfileDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (user.role == "VENDOR") Icons.Outlined.Storefront else Icons.Outlined.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color(0xFF3525CD),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (user.role == "VENDOR") "VENDOR / EO" else "PEMESAN TIKET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF3525CD),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
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
                        onClick = { showEditProfileDialog = true }
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Receipt,
                        title = "Riwayat Transaksi Saya",
                        onClick = onNavigateToRiwayat
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Favorite,
                        title = "Koleksi Favorit Saya",
                        onClick = onNavigateToWishlist
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Lock,
                        title = "Keamanan & Kata Sandi",
                        onClick = { showChangePasswordDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            AppButton(
                text = "Keluar dari Akun",
                onClick = onLogoutClick,
                variant = AppButtonVariant.SECONDARY_OUTLINED
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var newFullName by remember { mutableStateOf(user.fullName.orEmpty()) }
        var newRole by remember { mutableStateOf(user.role) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Ubah Profil & Data Diri",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Nama Lengkap", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = newFullName,
                        onValueChange = { newFullName = it },
                        placeholder = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Role Akun", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = newRole == "PEMESAN",
                            onClick = { newRole = "PEMESAN" },
                            label = { Text("Pemesan Tiket") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE2DFFF),
                                selectedLabelColor = Color(0xFF3525CD)
                            )
                        )
                        FilterChip(
                            selected = newRole == "VENDOR",
                            onClick = { newRole = "VENDOR" },
                            label = { Text("Vendor / EO") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE2DFFF),
                                selectedLabelColor = Color(0xFF3525CD)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newFullName.isNotBlank()) {
                            onUpdateProfile(newFullName, newRole)
                            Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            showEditProfileDialog = false
                        } else {
                            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold, color = Color(0xFF3525CD))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        var newPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = {
                Text(
                    text = "Keamanan & Kata Sandi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Kata Sandi Baru", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text("Minimal 6 karakter") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassword.length >= 6) {
                            onUpdatePassword(newPassword)
                            Toast.makeText(context, "Kata sandi diperbarui", Toast.LENGTH_SHORT).show()
                            showChangePasswordDialog = false
                        } else {
                            Toast.makeText(context, "Minimal 6 karakter", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Perbarui", fontWeight = FontWeight.Bold, color = Color(0xFF3525CD))
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
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
            onLogoutClick = {},
            onNavigateToRiwayat = {},
            onNavigateToWishlist = {},
            onUpdateProfile = { _, _ -> },
            onUpdatePassword = {}
        )
    }
}
