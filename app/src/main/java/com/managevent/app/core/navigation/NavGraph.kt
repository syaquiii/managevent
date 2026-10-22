package com.managevent.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.managevent.app.auth.ui.LoginScreen
import com.managevent.app.auth.ui.ProfileScreen
import com.managevent.app.auth.ui.RegisterScreen
import com.managevent.app.features.agenda.ui.AgendaFormScreen
import com.managevent.app.features.agenda.ui.AgendaListScreen
import com.managevent.app.features.katalog.ui.KatalogDetailScreen
import com.managevent.app.features.katalog.ui.KatalogFormScreen
import com.managevent.app.features.katalog.ui.KatalogListScreen
import com.managevent.app.features.pemesanan.ui.CheckoutScreen
import com.managevent.app.features.pemesanan.ui.DetailPemesananScreen
import com.managevent.app.features.pemesanan.ui.RiwayatPemesananScreen
import com.managevent.app.features.ulasan.ui.UlasanFormScreen
import com.managevent.app.features.ulasan.ui.UlasanListScreen
import com.managevent.app.features.wishlist.ui.WishlistDetailScreen
import com.managevent.app.features.wishlist.ui.WishlistScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.KatalogList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Routes
        composable(Screen.Login.route) {
            LoginScreen(onNavigateToRegister = { navController.navigate(Screen.Register.route) })
        }
        composable(Screen.Register.route) {
            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // Feature 1: Katalog (Muhammad Abdul Fatah)
        composable(Screen.KatalogList.route) {
            KatalogListScreen(
                onEventClick = { eventId -> navController.navigate(Screen.KatalogDetail.createRoute(eventId)) },
                onAddEventClick = { navController.navigate(Screen.KatalogForm.route) }
            )
        }
        composable(Screen.KatalogDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            KatalogDetailScreen(eventId = eventId)
        }
        composable(Screen.KatalogForm.route) {
            KatalogFormScreen()
        }

        // Feature 2: Pemesanan (Muhammad Darren Achilles Setiawan)
        composable(Screen.Checkout.route) {
            CheckoutScreen()
        }
        composable(Screen.RiwayatPemesanan.route) {
            RiwayatPemesananScreen()
        }
        composable(Screen.DetailPemesanan.route) { backStackEntry ->
            val pemesananId = backStackEntry.arguments?.getString("pemesananId") ?: ""
            DetailPemesananScreen(pemesananId = pemesananId)
        }

        // Feature 3: Ulasan (Rayhan Wira Pamungkas)
        composable(Screen.UlasanList.route) {
            UlasanListScreen()
        }
        composable(Screen.UlasanForm.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            UlasanFormScreen(eventId = eventId)
        }

        // Feature 4: Wishlist (Muhammad Syauqi Imaduddin)
        composable(Screen.Wishlist.route) {
            WishlistScreen()
        }
        composable(Screen.WishlistDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            WishlistDetailScreen(eventId = eventId)
        }

        // Feature 5: Agenda (Farrel Radya Putra)
        composable(Screen.AgendaList.route) {
            AgendaListScreen()
        }
        composable(Screen.AgendaForm.route) {
            AgendaFormScreen()
        }
    }
}
