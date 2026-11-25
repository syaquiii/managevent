package com.managevent.app.auth.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.InputFieldBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.Outline
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.util.UiState

@Composable
fun ProfileScreen(
    user: UserModel?,
    modifier: Modifier = Modifier,
    actionState: UiState<String> = UiState.Idle,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNavigateToRiwayat: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onUpdateProfile: (String, String) -> Unit = { _, _ -> },
    onUpdatePassword: (String) -> Unit = {},
    onClearActionState: () -> Unit = {}
) {
    val context = LocalContext.current
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        when (actionState) {
            is UiState.Success -> {
                Toast.makeText(context, actionState.data, Toast.LENGTH_SHORT).show()
                showEditProfileDialog = false
                showChangePasswordDialog = false
                onClearActionState()
            }
            is UiState.Error -> {
                Toast.makeText(context, actionState.message, Toast.LENGTH_SHORT).show()
                onClearActionState()
            }
            else -> {}
        }
    }

    ProfileContent(
        user = user,
        actionState = actionState,
        showEditProfileDialog = showEditProfileDialog,
        showChangePasswordDialog = showChangePasswordDialog,
        onShowEditProfileDialog = { showEditProfileDialog = it },
        onShowChangePasswordDialog = { showChangePasswordDialog = it },
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick,
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
    user: UserModel?,
    actionState: UiState<String>,
    showEditProfileDialog: Boolean,
    showChangePasswordDialog: Boolean,
    onShowEditProfileDialog: (Boolean) -> Unit,
    onShowChangePasswordDialog: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onUpdatePassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUpdating = actionState is UiState.Loading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppTopBar(title = "Profil Pengguna") },
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        if (user == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(ActiveFilterChipBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOutline,
                        contentDescription = "Mode Tamu",
                        tint = PrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Anda Belum Masuk",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masuk atau buat akun untuk memesan tiket, menyimpan favorit, menulis ulasan, dan mengelola agenda event.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                AppButton(
                    text = "Masuk Akun",
                    onClick = onLoginClick,
                    variant = AppButtonVariant.CONVERSION_CTA
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    text = "Daftar Akun Baru",
                    onClick = onRegisterClick,
                    variant = AppButtonVariant.SECONDARY_OUTLINED
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clickable { onShowEditProfileDialog(true) },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(ActiveFilterChipBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto Profil",
                            tint = PrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah Profil",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (!user.fullName.isNullOrBlank()) user.fullName else "Pengguna Managevent",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        fontSize = 20.sp
                    )
                )

                Text(
                    text = user.email.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = ActiveFilterChipBackground,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { onShowEditProfileDialog(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (user.role == "VENDOR") Icons.Outlined.Storefront else Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            tint = PrimaryContainer,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (user.role == "VENDOR") "VENDOR / EO" else "PEMESAN TIKET",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

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
                            onClick = { onShowEditProfileDialog(true) }
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
                            onClick = { onShowChangePasswordDialog(true) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(
                    text = "Keluar dari Akun",
                    onClick = onLogoutClick,
                    variant = AppButtonVariant.SECONDARY_OUTLINED
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showEditProfileDialog && user != null) {
        var newFullName by remember(user) { mutableStateOf(user.fullName.orEmpty()) }
        var newRole by remember(user) { mutableStateOf(user.role) }

        AlertDialog(
            onDismissRequest = { if (!isUpdating) onShowEditProfileDialog(false) },
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
                        singleLine = true,
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
                                selectedContainerColor = ActiveFilterChipBackground,
                                selectedLabelColor = PrimaryContainer
                            )
                        )
                        FilterChip(
                            selected = newRole == "VENDOR",
                            onClick = { newRole = "VENDOR" },
                            label = { Text("Vendor / EO") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ActiveFilterChipBackground,
                                selectedLabelColor = PrimaryContainer
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isUpdating,
                    onClick = {
                        if (newFullName.trim().isNotBlank()) {
                            onUpdateProfile(newFullName.trim(), newRole)
                        } else {
                            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        text = if (isUpdating) "Menyimpan..." else "Simpan",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isUpdating,
                    onClick = { onShowEditProfileDialog(false) }
                ) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showChangePasswordDialog && user != null) {
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isUpdating) onShowChangePasswordDialog(false) },
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
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Tampilkan Kata Sandi",
                                    tint = Outline
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("Konfirmasi Kata Sandi", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Ulangi kata sandi baru") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isUpdating,
                    onClick = {
                        when {
                            newPassword.length < 6 ->
                                Toast.makeText(context, "Kata sandi minimal 6 karakter", Toast.LENGTH_SHORT).show()
                            newPassword != confirmPassword ->
                                Toast.makeText(context, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
                            else -> onUpdatePassword(newPassword)
                        }
                    }
                ) {
                    Text(
                        text = if (isUpdating) "Memperbarui..." else "Perbarui",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isUpdating,
                    onClick = { onShowChangePasswordDialog(false) }
                ) {
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
                    .background(InputFieldBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = OnBackground
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Buka",
            tint = Outline,
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
            actionState = UiState.Idle,
            showEditProfileDialog = false,
            showChangePasswordDialog = false,
            onShowEditProfileDialog = {},
            onShowChangePasswordDialog = {},
            onLoginClick = {},
            onRegisterClick = {},
            onLogoutClick = {},
            onNavigateToRiwayat = {},
            onNavigateToWishlist = {},
            onUpdateProfile = { _, _ -> },
            onUpdatePassword = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileGuestContentPreview() {
    ManageventTheme {
        ProfileContent(
            user = null,
            actionState = UiState.Idle,
            showEditProfileDialog = false,
            showChangePasswordDialog = false,
            onShowEditProfileDialog = {},
            onShowChangePasswordDialog = {},
            onLoginClick = {},
            onRegisterClick = {},
            onLogoutClick = {},
            onNavigateToRiwayat = {},
            onNavigateToWishlist = {},
            onUpdateProfile = { _, _ -> },
            onUpdatePassword = {}
        )
    }
}
