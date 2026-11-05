package com.managevent.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.managevent.app.auth.ui.LoginScreen
import com.managevent.app.auth.ui.OnboardingScreen
import com.managevent.app.auth.ui.ProfileScreen
import com.managevent.app.auth.ui.RegisterScreen
import com.managevent.app.features.agenda.ui.AgendaFormScreen
import com.managevent.app.features.agenda.ui.AgendaListScreen
import com.managevent.app.features.agenda.ui.BookedEventListScreen
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
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Routes
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkipClick = { navController.navigate(Screen.KatalogList.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onFinishOnboarding = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.KatalogList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
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
            KatalogDetailScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onBookClick = { id -> navController.navigate(Screen.Checkout.createRoute(id)) },
                onReviewClick = { id -> navController.navigate(Screen.UlasanList.createRoute(id)) }
            )
        }
        composable(Screen.KatalogForm.route) {
            KatalogFormScreen(
                onBackClick = { navController.popBackStack() },
                onSuccessSubmit = { navController.popBackStack() }
            )
        }

        // Feature 2: Pemesanan (Muhammad Darren Achilles Setiawan)
        composable(Screen.Checkout.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
            CheckoutScreen(
                eventId = eventId,
                onBackClick = { navController.popBackStack() },
                onSuccessSubmit = {
                    navController.navigate(Screen.RiwayatPemesanan.route) {
                        popUpTo(Screen.KatalogList.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.RiwayatPemesanan.route) {
            RiwayatPemesananScreen(
                onBackClick = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screen.KatalogList.route) {
                            popUpTo(Screen.KatalogList.route) { inclusive = true }
                        }
                    }
                },
                onTransactionClick = { pemesananId ->
                    navController.navigate(Screen.DetailPemesanan.createRoute(pemesananId))
                }
            )
        }
        composable(Screen.DetailPemesanan.route) { backStackEntry ->
            val pemesananId = backStackEntry.arguments?.getString("pemesananId") ?: ""
            DetailPemesananScreen(
                pemesananId = pemesananId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Feature 3: Ulasan (Rayhan Wira Pamungkas)
        composable(Screen.UlasanList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "dummy-event-id"
            UlasanListScreen(
                eventId = eventId,
                onAddUlasanClick = {
                    navController.navigate(Screen.UlasanForm.createRoute(eventId))
                }
            )
        }
        composable(Screen.UlasanForm.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            UlasanFormScreen(
                eventId = eventId,
                onSuccessNavigateBack = { navController.popBackStack() }
            )
        }

        // Feature 4: Wishlist (Muhammad Syauqi Imaduddin)
        composable(Screen.Wishlist.route) {
            WishlistScreen(
                onEventClick = { eventId ->
                    navController.navigate(Screen.KatalogDetail.createRoute(eventId))
                }
            )
        }
        composable(Screen.WishlistDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            WishlistDetailScreen(eventId = eventId)
        }

        // Feature 5: Agenda (Farrel Radya Putra)
        composable(Screen.AgendaList.route) {
            BookedEventListScreen(
                onOpenNotes = { eventId ->
                    navController.navigate(Screen.AgendaDetailList.createRoute(eventId))
                }
            )
        }
        composable(Screen.AgendaDetailList.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            AgendaListScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onAddNote = { navController.navigate(Screen.AgendaForm.route) }
            )
        }
        composable(Screen.AgendaForm.route) {
            AgendaFormScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
