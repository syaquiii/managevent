package com.managevent.app.features.ulasan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppTopBar

@Composable
fun UlasanFormScreen(
    eventId: String = ""
) {
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar(title = "Beri Ulasan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Beri Ulasan untuk Event ID: $eventId")
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Tuliskan ulasan Anda") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = "Kirim Ulasan",
                onClick = { /* TODO: Kirim ulasan */ }
            )
        }
    }
}
