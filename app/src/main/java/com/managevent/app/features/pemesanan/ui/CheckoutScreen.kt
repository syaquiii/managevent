package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.managevent.app.core.components.AppButton
import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun CheckoutScreen(
    modifier: Modifier = Modifier
) {
    CheckoutContent(
        onPayClick = { /* TODO: Process Payment */ },
        modifier = modifier
    )
}

@Composable
fun CheckoutContent(
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Checkout Pemesanan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Ringkasan Pesanan Event")
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = "Bayar Sekarang",
                onClick = onPayClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutContentPreview() {
    ManageventTheme {
        CheckoutContent(onPayClick = {})
    }
}
