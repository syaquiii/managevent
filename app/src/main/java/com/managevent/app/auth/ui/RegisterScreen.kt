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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.managevent.app.auth.viewmodel.AuthViewModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("PEMESAN") } // "PEMESAN" atau "VENDOR"

    RegisterContent(
        fullName = fullName,
        onFullNameChange = { fullName = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        selectedRole = selectedRole,
        onRoleChange = { selectedRole = it },
        onRegisterClick = {
            viewModel.register(email, password, fullName, selectedRole)
            onRegisterSuccess()
        },
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
fun RegisterContent(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    selectedRole: String,
    onRoleChange: (String) -> Unit,
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

            // Brand Header
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
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF181445),
                    fontSize = 26.sp
                )
            )

            Text(
                text = "Daftar untuk menikmati akses penuh event dan fitur menarik.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF464555),
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            // RBAC Role Selection Segment (Pemesan vs Vendor)
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
                val roles = listOf("PEMESAN" to "🎫 Pemesan Tiket", "VENDOR" to "🎪 Vendor / EO")
                roles.forEach { (roleKey, roleLabel) ->
                    val isSelected = selectedRole == roleKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { onRoleChange(roleKey) },
                        label = {
                            Text(
                                text = roleLabel,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE2DFFF),
                            selectedLabelColor = Color(0xFF3525CD),
                            containerColor = Color(0xFFF6F2FF),
                            labelColor = Color(0xFF464555)
                        ),
                        border = null,
                        modifier = Modifier.weight(1f).height(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name Input
            Text(
                text = "Nama Lengkap",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                placeholder = { Text("Nama Lengkap Anda") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Input
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("nama@email.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181445)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Minimal 6 karakter") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3525CD),
                    unfocusedContainerColor = Color(0xFFF6F2FF),
                    focusedContainerColor = Color(0xFFF6F2FF)
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Register Button
            AppButton(
                text = "Daftar Akun",
                onClick = onRegisterClick,
                variant = AppButtonVariant.STANDARD_PRIMARY
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
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
            email = "sarah@example.com",
            onEmailChange = {},
            password = "password123",
            onPasswordChange = {},
            selectedRole = "PEMESAN",
            onRoleChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {}
        )
    }
}
