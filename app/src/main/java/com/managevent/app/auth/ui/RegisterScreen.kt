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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.managevent.app.auth.data.RegisterResult
import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.theme.ActiveFilterChipBackground
import com.managevent.app.core.theme.Background
import com.managevent.app.core.theme.InputFieldBackground
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.theme.OnBackground
import com.managevent.app.core.theme.OnSurfaceVariant
import com.managevent.app.core.theme.Outline
import com.managevent.app.core.theme.PrimaryContainer
import com.managevent.app.core.theme.SecondaryContainer
import com.managevent.app.core.util.UiState

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerState: UiState<RegisterResult> = UiState.Idle,
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onRegistrationComplete: (requiresEmailConfirmation: Boolean) -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("PEMESAN") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(registerState) {
        if (registerState is UiState.Error) {
            Toast.makeText(context, registerState.message, Toast.LENGTH_SHORT).show()
        }
    }

    RegisterContent(
        fullName = fullName,
        onFullNameChange = {
            fullName = it
            fullNameError = null
        },
        fullNameError = fullNameError,
        email = email,
        onEmailChange = {
            email = it
            emailError = null
        },
        emailError = emailError,
        password = password,
        onPasswordChange = {
            password = it
            passwordError = null
        },
        passwordError = passwordError,
        selectedRole = selectedRole,
        onRoleChange = { selectedRole = it },
        registerState = registerState,
        onRegisterClick = {
            var hasError = false
            val trimmedName = fullName.trim()
            val trimmedEmail = email.trim()
            if (trimmedName.isBlank()) {
                fullNameError = "Harap isi nama lengkap Anda"
                hasError = true
            }
            if (trimmedEmail.isBlank()) {
                emailError = "Harap isi email Anda"
                hasError = true
            } else if (!trimmedEmail.contains("@")) {
                emailError = "Format email tidak valid"
                hasError = true
            }
            if (password.isBlank()) {
                passwordError = "Harap isi password Anda"
                hasError = true
            } else if (password.length < 6) {
                passwordError = "Password minimal 6 karakter"
                hasError = true
            }

            if (!hasError) {
                onRegisterClick(trimmedEmail, password, trimmedName, selectedRole)
            }
        },
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )

    if (registerState is UiState.Success) {
        val result = registerState.data
        val user = result.user
        val roleTitle = if (user.role == "VENDOR") "Vendor / EO" else "Pemesan Tiket"
        val displayName = user.fullName?.takeIf { it.isNotBlank() } ?: "Pengguna"

        AlertDialog(
            onDismissRequest = { onRegistrationComplete(result.requiresEmailConfirmation) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ActiveFilterChipBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (result.requiresEmailConfirmation) "Verifikasi Email Anda" else "Registrasi Berhasil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            },
            text = {
                Text(
                    text = if (result.requiresEmailConfirmation) {
                        "Akun $displayName sebagai $roleTitle berhasil dibuat. Silakan cek kotak masuk email ${user.email} untuk konfirmasi sebelum masuk."
                    } else {
                        "Selamat datang, $displayName! Akun Anda sebagai $roleTitle telah aktif dan siap digunakan."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                AppButton(
                    text = if (result.requiresEmailConfirmation) "Ke Halaman Masuk" else "Mulai Jelajahi Event",
                    onClick = { onRegistrationComplete(result.requiresEmailConfirmation) },
                    variant = AppButtonVariant.CONVERSION_CTA
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    fullNameError: String?,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    selectedRole: String,
    onRoleChange: (String) -> Unit,
    registerState: UiState<RegisterResult>,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.ime),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainer)
                    )
                    Text(
                        text = "MANAGEVENT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryContainer,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Buat Akun",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        fontSize = 24.sp
                    )
                )

                Text(
                    text = "Daftar untuk pesan tiket dan kelola persiapan event Anda.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                if (registerState is UiState.Error) {
                    Text(
                        text = registerState.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Text(
                    text = "Daftar Sebagai",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val roles = listOf(
                        Triple("PEMESAN", "Pemesan Tiket", Icons.Outlined.ConfirmationNumber),
                        Triple("VENDOR", "Vendor / EO", Icons.Outlined.Storefront)
                    )
                    roles.forEach { (roleKey, roleLabel, roleIcon) ->
                        val isSelected = selectedRole == roleKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { onRoleChange(roleKey) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = roleIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isSelected) PrimaryContainer else OnSurfaceVariant
                                    )
                                    Text(
                                        text = roleLabel,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ActiveFilterChipBackground,
                                selectedLabelColor = PrimaryContainer,
                                containerColor = InputFieldBackground,
                                labelColor = OnSurfaceVariant
                            ),
                            border = null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Nama Lengkap",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (fullNameError != null) MaterialTheme.colorScheme.error else OnBackground
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    placeholder = { Text("Nama Lengkap Anda") },
                    isError = fullNameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryContainer,
                        unfocusedContainerColor = InputFieldBackground,
                        focusedContainerColor = InputFieldBackground,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorContainerColor = Color(0xFFFFF0F0)
                    )
                )
                if (fullNameError != null) {
                    Text(
                        text = fullNameError,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (emailError != null) MaterialTheme.colorScheme.error else OnBackground
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("nama@email.com") },
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryContainer,
                        unfocusedContainerColor = InputFieldBackground,
                        focusedContainerColor = InputFieldBackground,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorContainerColor = Color(0xFFFFF0F0)
                    )
                )
                if (emailError != null) {
                    Text(
                        text = emailError,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (passwordError != null) MaterialTheme.colorScheme.error else OnBackground
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("Minimal 6 karakter") },
                    isError = passwordError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Tampilkan Password",
                                tint = Outline
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryContainer,
                        unfocusedContainerColor = InputFieldBackground,
                        focusedContainerColor = InputFieldBackground,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorContainerColor = Color(0xFFFFF0F0)
                    )
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (registerState is UiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryContainer)
                    }
                } else {
                    AppButton(
                        text = "Daftar",
                        onClick = onRegisterClick,
                        variant = AppButtonVariant.STANDARD_PRIMARY
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sudah punya akun? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                    )
                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterContentPreview() {
    ManageventTheme {
        RegisterContent(
            fullName = "Sarah Wijaya",
            onFullNameChange = {},
            fullNameError = null,
            email = "sarah@example.com",
            onEmailChange = {},
            emailError = null,
            password = "password123",
            onPasswordChange = {},
            passwordError = null,
            selectedRole = "PEMESAN",
            onRoleChange = {},
            registerState = UiState.Idle,
            onRegisterClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterSuccessDialogPreview() {
    ManageventTheme {
        RegisterScreen(
            registerState = UiState.Success(
                RegisterResult(
                    user = UserModel(
                        id = "usr-1",
                        email = "sarah@example.com",
                        fullName = "Sarah Wijaya",
                        role = "PEMESAN"
                    ),
                    requiresEmailConfirmation = false
                )
            )
        )
    }
}
