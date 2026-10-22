package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppTopBar

@Composable
fun RiwayatPemesananScreen() {
    Scaffold(
        topBar = { AppTopBar(title = "Riwayat Pemesanan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Daftar Transaksi Tiket")
            LazyColumn {
                items(3) { index ->
                    Text(
                        text = "Transaksi #${index + 1} - Lunas",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
