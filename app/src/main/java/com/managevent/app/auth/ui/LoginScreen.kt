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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppButtonVariant
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
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginState: UiState<UserModel> = UiState.Idle,
    resetPasswordState: UiState<String> = UiState.Idle,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: (String) -> Unit = {},
    onClearResetState: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(loginState) {
        if (loginState is UiState.Error) {
            Toast.makeText(context, loginState.message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is UiState.Success -> {
                Toast.makeText(context, resetPasswordState.data, Toast.LENGTH_LONG).show()
                showForgotDialog = false
                onClearResetState()
            }
            is UiState.Error -> {
                Toast.makeText(context, resetPasswordState.message, Toast.LENGTH_SHORT).show()
                onClearResetState()
            }
            else -> {}
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
            val trimmedEmail = email.trim()
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
                onLoginClick(trimmedEmail, password)
            }
        },
        onForgotPasswordClick = { showForgotDialog = true },
        onNavigateToRegister = onNavigateToRegister,
        onBackClick = onBackClick,
        modifier = modifier
    )

    if (showForgotDialog) {
        var resetEmail by remember(email) { mutableStateOf(email.trim()) }
        var resetEmailError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                if (resetPasswordState !is UiState.Loading) showForgotDialog = false
            },
            title = {
                Text(
                    text = "Reset Kata Sandi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Masukkan email akun Anda. Kami akan mengirimkan tautan pemulihan kata sandi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = {
                            resetEmail = it
                            resetEmailError = null
                        },
                        placeholder = { Text("nama@email.com") },
                        isError = resetEmailError != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (resetEmailError != null) {
                        Text(
                            text = resetEmailError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = resetPasswordState !is UiState.Loading,
                    onClick = {
                        val trimmed = resetEmail.trim()
                        if (trimmed.isBlank() || !trimmed.contains("@")) {
                            resetEmailError = "Format email tidak valid"
                        } else {
                            onForgotPasswordClick(trimmed)
                        }
                    }
                ) {
                    Text(
                        text = if (resetPasswordState is UiState.Loading) "Mengirim..." else "Kirim Tautan",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainer
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = resetPasswordState !is UiState.Loading,
                    onClick = { showForgotDialog = false }
                ) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isServerAuthError = loginState is UiState.Error

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (onBackClick != null) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            }
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Masuk Akun",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBackground,
                        fontSize = 24.sp
                    )
                )

                Text(
                    text = "Kelola event, tiket, dan pesanan Anda dalam satu aplikasi.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

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

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (emailError != null || isServerAuthError) MaterialTheme.colorScheme.error else OnBackground
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (passwordError != null || isServerAuthError) MaterialTheme.colorScheme.error else OnBackground
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Lupa Password?",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onForgotPasswordClick() }
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
                        CircularProgressIndicator(color = SecondaryContainer)
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
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
                    )
                    Text(
                        text = "Daftar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryContainer,
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
            onForgotPasswordClick = {},
            onNavigateToRegister = {}
        )
    }
}
