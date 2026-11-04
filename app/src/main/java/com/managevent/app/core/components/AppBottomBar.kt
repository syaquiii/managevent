package com.managevent.app.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.managevent.app.core.navigation.Screen
import com.managevent.app.core.theme.ManageventTheme

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            title = "Beranda",
            route = Screen.KatalogList.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Favorit",
            route = Screen.Wishlist.route,
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder
        ),
        BottomNavItem(
            title = "Pesanan",
            route = Screen.RiwayatPemesanan.route,
            selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
            unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong
        ),
        BottomNavItem(
            title = "Catatan",
            route = Screen.AgendaList.route,
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth
        ),
        BottomNavItem(
            title = "Profil",
            route = Screen.Profile.route,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.PersonOutline
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3F51B5),
                    selectedTextColor = Color(0xFF3F51B5),
                    indicatorColor = Color(0xFFEDE7F6),
                    unselectedIconColor = Color(0xFF757575),
                    unselectedTextColor = Color(0xFF757575)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    ManageventTheme {
        AppBottomBar(
            currentRoute = Screen.KatalogList.route,
            onNavigate = {}
        )
    }
}
