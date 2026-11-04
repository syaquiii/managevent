package com.managevent.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.managevent.app.core.navigation.AppNavHost
import com.managevent.app.core.navigation.Screen
import com.managevent.app.core.theme.ManageventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageventTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Sembunyikan BottomBar di layar Form Catatan
                val showBottomBar = currentRoute != Screen.AgendaForm.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.KatalogList.route,
                                    onClick = {
                                        if (currentRoute != Screen.KatalogList.route) {
                                            navController.navigate(Screen.KatalogList.route) {
                                                popUpTo(Screen.AgendaList.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text("Beranda") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Wishlist.route,
                                    onClick = {
                                        if (currentRoute != Screen.Wishlist.route) {
                                            navController.navigate(Screen.Wishlist.route) {
                                                popUpTo(Screen.AgendaList.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                    label = { Text("Favorit") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.RiwayatPemesanan.route,
                                    onClick = {
                                        if (currentRoute != Screen.RiwayatPemesanan.route) {
                                            navController.navigate(Screen.RiwayatPemesanan.route) {
                                                popUpTo(Screen.AgendaList.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                                    label = { Text("Pesanan") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.AgendaList.route || currentRoute == Screen.AgendaDetailList.route,
                                    onClick = {
                                        if (currentRoute != Screen.AgendaList.route) {
                                            navController.navigate(Screen.AgendaList.route) {
                                                popUpTo(Screen.AgendaList.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.AutoMirrored.Outlined.EventNote, contentDescription = null) },
                                    label = { Text("Catatan") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Profile.route,
                                    onClick = {
                                        if (currentRoute != Screen.Profile.route) {
                                            navController.navigate(Screen.Profile.route) {
                                                popUpTo(Screen.AgendaList.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                                    label = { Text("Profil") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        startDestination = Screen.AgendaList.route,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
