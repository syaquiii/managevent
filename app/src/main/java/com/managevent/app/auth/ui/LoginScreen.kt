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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
import com.managevent.app.core.theme.ManageventTheme
import com.managevent.app.core.util.UiState

@Composable
fun LoginScreen(
    loginState: UiState<UserModel> = UiState.Idle,
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState is UiState.Error) {
            Toast.makeText(context, loginState.message, Toast.LENGTH_SHORT).show()
        }
    }

    LoginContent(
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
        loginState = loginState,
        onLoginClick = {
            var hasError = false
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
                onLoginClick(email, password)
            }
        },
        onNavigateToRegister = onNavigateToRegister,
        modifier = modifier
    )
}

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    loginState: UiState<UserModel>,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isServerAuthError = loginState is UiState.Error

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFFCF8FF),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                    .padding(horizontal = 24.dp, vertical = 16.dp)
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
                    text = "Masuk Akun",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF181445),
                        fontSize = 24.sp
                    )
                )

                Text(
                    text = "Kelola event, tiket, dan pesanan Anda dalam satu aplikasi.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF464555),
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Top Server Error Banner (Single display)
                if (loginState is UiState.Error) {
                    Text(
                        text = loginState.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Email Input Field
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (emailError != null || isServerAuthError) MaterialTheme.colorScheme.error else Color(0xFF181445)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("nama@email.com") },
                    isError = emailError != null || isServerAuthError,
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
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input Field
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (passwordError != null || isServerAuthError) MaterialTheme.colorScheme.error else Color(0xFF181445)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("••••••••") },
                    isError = passwordError != null || isServerAuthError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Tampilkan Password",
                                tint = Color(0xFF777587)
                            )
                        }
                    },
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
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Lupa Password?",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF3525CD),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (loginState is UiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFC6955))
                    }
                } else {
                    AppButton(
                        text = "Masuk",
                        onClick = onLoginClick,
                        variant = AppButtonVariant.CONVERSION_CTA
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Belum punya akun? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF464555))
                    )
                    Text(
                        text = "Daftar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF3525CD),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    ManageventTheme {
        LoginContent(
            email = "user@example.com",
            onEmailChange = {},
            emailError = null,
            password = "password123",
            onPasswordChange = {},
            passwordError = null,
            loginState = UiState.Idle,
            onLoginClick = {},
            onNavigateToRegister = {}
        )
    }
}
