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
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState

@Composable
fun RegisterScreen(
    registerState: UiState<UserModel> = UiState.Idle,
    modifier: Modifier = Modifier,
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
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
            val msg = registerState.message.lowercase()
            if (msg.contains("nama")) fullNameError = registerState.message
            if (msg.contains("email")) emailError = registerState.message
            if (msg.contains("password")) passwordError = registerState.message
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
            if (fullName.isBlank()) {
                fullNameError = "Harap isi nama lengkap Anda"
                hasError = true
            }
            if (email.isBlank()) {
                emailError = "Harap isi email Anda"
                hasError = true
            } else if (!email.contains("@")) {
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
                onRegisterClick(email, password, fullName, selectedRole)
            }
        },
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

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
    registerState: UiState<UserModel>,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFCF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFC6955))
                )
                Text(
                    text = "MANAGEVENT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3525CD),
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Buat Akun",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF181445),
                    fontSize = 26.sp
                )
            )

            Text(
                text = "Daftar untuk pesan tiket dan kelola persiapan event Anda.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF464555),
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            if (registerState is UiState.Error) {
                Text(
                    text = registerState.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Text(
                text = "Daftar Sebagai",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) Color(0xFF3525CD) else Color(0xFF464555)
                                )
                                Text(
                                    text = roleLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE2DFFF),
                            selectedLabelColor = Color(0xFF3525CD),
                            containerColor = Color(0xFFF6F2FF),
                            labelColor = Color(0xFF464555)
                        ),
                        border = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name Field
            Text(
                text = "Nama Lengkap",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (fullNameError != null) MaterialTheme.colorScheme.error else Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                placeholder = { Text("Nama Lengkap Anda") },
                isError = fullNameError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = Color(0xFFFFF0F0)
                )
            )
            if (fullNameError != null) {
                Text(
                    text = fullNameError,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (emailError != null) MaterialTheme.colorScheme.error else Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("nama@email.com") },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = Color(0xFFFFF0F0)
                )
            )
            if (emailError != null) {
                Text(
                    text = emailError,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (passwordError != null) MaterialTheme.colorScheme.error else Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Minimal 6 karakter") },
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = Color(0xFFFFF0F0)
                )
            )
            if (passwordError != null) {
                Text(
                    text = passwordError,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (registerState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3525CD))
                }
            } else {
                AppButton(
                    text = "Daftar",
                    onClick = onRegisterClick,
                    variant = AppButtonVariant.STANDARD_PRIMARY
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF464555))
                )
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF3525CD),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
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
