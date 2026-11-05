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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.theme.ManageventTheme
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val titlePart1: String,
    val titlePart2: String,
    val titleHighlight: String,
    val description: String,
    val pageType: Int // 1 = Tiket, 2 = Catatan, 3 = Ulasan
)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onSkipClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onFinishOnboarding: () -> Unit = {}
) {
    OnboardingContent(
        onSkipClick = onSkipClick,
        onLoginClick = onLoginClick,
        onFinishOnboarding = onFinishOnboarding,
        modifier = modifier
    )
}

@Composable
fun OnboardingContent(
    onSkipClick: () -> Unit,
    onLoginClick: () -> Unit,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPageData(
            titlePart1 = "Akses Mudah",
            titlePart2 = "Untuk Setiap Konser &",
            titleHighlight = "Festival Impian",
            description = "Beli tiket resmi, nikmati penawaran eksklusif, dan jelajahi ratusan panggung musik terbaik tanpa calo.",
            pageType = 1
        ),
        OnboardingPageData(
            titlePart1 = "Kelola Catatan &",
            titlePart2 = "Persiapan Acara Lebih",
            titleHighlight = "Terorganisir",
            description = "Buat daftar persiapan event, pantau jadwal checklist, dan koordinasikan rencana Anda dengan sempurna.",
            pageType = 2
        ),
        OnboardingPageData(
            titlePart1 = "Ulasan Transparan",
            titlePart2 = "& Komunitas Vendor",
            titleHighlight = "Terpercaya",
            description = "Temukan ulasan jujur dari ribuan pengunjung lainnya dan pilih vendor terbaik untuk event Anda.",
            pageType = 3
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F5FF),
        bottomBar = {
            // Footer Navigation Row (Pinned at bottom so it's NEVER cut off)
            Surface(
                color = Color(0xFFF7F5FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 8.dp),
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
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // 1. Top Bar Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                                color = Color(0xFF181445),
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    TextButton(onClick = onSkipClick) {
                        Text(
                            text = "Lewati",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF464555),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // 2. Multi-page HorizontalPager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->
                    val pageData = pages[pageIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 3D Graphic Composition Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF262242), Color(0xFF141224))
                                        )
                                    )
                            ) {
                                when (pageData.pageType) {
                                    1 -> OnboardingGraphicPage1()
                                    2 -> OnboardingGraphicPage2()
                                    else -> OnboardingGraphicPage3()
                                }

                                // Top-Left Pill: Official Partner
                                Surface(
                                    color = Color.White.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = "★", color = Color(0xFFFF9800), fontSize = 11.sp)
                                        Text(
                                            text = "Official Partner",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF181445),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                // Bottom-Right Pill: 100% Bebas Calo
                                Surface(
                                    color = Color.White.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF005338))
                                        )
                                        Text(
                                            text = "100% Bebas Calo",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF005338),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Headline Title & Subtitle
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = pageData.titlePart1,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF181445),
                                        fontSize = 26.sp,
                                        lineHeight = 32.sp
                                    )
                                )
                                Text(
                                    text = pageData.titlePart2,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF181445),
                                        fontSize = 26.sp,
                                        lineHeight = 32.sp
                                    )
                                )
                                Text(
                                    text = pageData.titleHighlight,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3525CD),
                                        fontSize = 26.sp,
                                        lineHeight = 32.sp
                                    )
                                )
                            }

                            Text(
                                text = pageData.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF464555),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Dynamic Indicator Dots & Action Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Pager Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { dotIndex ->
                        val isSelected = pagerState.currentPage == dotIndex
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 28.dp else 8.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) Color(0xFF3525CD) else Color(0xFFE2DFFF))
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(dotIndex)
                                    }
                                }
                        )
                    }
                }

                // Circle Coral Arrow Button
                IconButton(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFC6955))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Selanjutnya",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingGraphicPage1() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Coral Ticket
        Box(
            modifier = Modifier
                .offset(x = (-10).dp, y = 20.dp)
                .rotate(-12f)
                .width(170.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFF8A7A))
                .padding(10.dp)
        ) {
            Column {
                Text("EUPHORIA FEST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("JULY 19-21 • SEAT 4A", color = Color.White.copy(alpha = 0.9f), fontSize = 9.sp)
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WEEKEND PASS", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 9.sp)
                }
            }
        }

        // Violet VIP Ticket
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 20.dp, y = 10.dp)
                .rotate(10f)
                .width(180.dp)
                .height(85.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF635BFF))
                .padding(10.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("VIP ACCESS PASS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("ALL AREAS ACCESS", color = Color.White.copy(alpha = 0.85f), fontSize = 9.sp)
                Text("SEAT 12B • LIVE CONCERT", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Gold VIP Coin Badge
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 20.dp, y = (-20).dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD166)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF8D5B00), modifier = Modifier.size(18.dp))
                Text("VIP", fontWeight = FontWeight.ExtraBold, color = Color(0xFF8D5B00), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun OnboardingGraphicPage2() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF3525CD))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EventNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CHECKLIST ACARA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF80C5A3), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sewa Gedung JCC", color = Color.White, fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF80C5A3), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Catering & Dokumentasi", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun OnboardingGraphicPage3() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFC6955))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    repeat(5) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("4.9 / 5.0 RATING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Ribuan Testimoni Verified", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingContentPreview() {
    ManageventTheme {
        OnboardingContent(
            onSkipClick = {},
            onLoginClick = {},
            onFinishOnboarding = {}
        )
    }
}
