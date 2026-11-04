package com.managevent.app.features.pemesanan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import com.managevent.app.core.components.AppTopBar
import com.managevent.app.core.theme.ManageventTheme

@Composable
fun RiwayatPemesananScreen(
    modifier: Modifier = Modifier
) {
    RiwayatPemesananContent(
        modifier = modifier
    )
}

@Composable
fun RiwayatPemesananContent(
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Berjalan", "Selesai")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Riwayat Pemesanan") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // TabRow + ViewPager (HorizontalPager)
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val statusTitle = tabs[page]
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(
                        items = listOf("1", "2", "3"),
                        key = { it }
                    ) { item ->
                        Text(
                            text = "Pesanan #$item - Status: $statusTitle",
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiwayatPemesananContentPreview() {
    ManageventTheme {
        RiwayatPemesananContent()
    }
}
