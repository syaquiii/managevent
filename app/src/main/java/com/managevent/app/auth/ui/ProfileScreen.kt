package com.managevent.app.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppButton

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Profil Pengguna")

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Email: user@managevent.com")

        Spacer(modifier = Modifier.height(32.dp))
        AppButton(
            text = "Keluar (Logout)",
            onClick = { /* TODO: Logout */ }
        )
    }
}
