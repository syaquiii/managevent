package com.managevent.app.core.navigation

sealed class Screen(val route: String) {
    // Auth Routes
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Profile : Screen("profile")

    // Feature 1: Katalog (Muhammad Abdul Fatah)
    data object KatalogList : Screen("katalog_list")
    data object KatalogForm : Screen("katalog_form")
    data object KatalogDetail : Screen("katalog_detail/{eventId}") {
        fun createRoute(eventId: String) = "katalog_detail/$eventId"
    }

    // Feature 2: Pemesanan (Muhammad Darren Achilles Setiawan)
    data object Checkout : Screen("checkout")
    data object RiwayatPemesanan : Screen("riwayat_pemesanan")
    data object DetailPemesanan : Screen("detail_pemesanan/{pemesananId}") {
        fun createRoute(pemesananId: String) = "detail_pemesanan/$pemesananId"
    }

    // Feature 3: Ulasan (Rayhan Wira Pamungkas)
    data object UlasanList : Screen("ulasan_list")
    data object UlasanForm : Screen("ulasan_form/{eventId}") {
        fun createRoute(eventId: String) = "ulasan_form/$eventId"
    }

    // Feature 4: Wishlist (Muhammad Syauqi Imaduddin)
    data object Wishlist : Screen("wishlist")
    data object WishlistDetail : Screen("wishlist_detail/{eventId}") {
        fun createRoute(eventId: String) = "wishlist_detail/$eventId"
    }

    // Feature 5: Agenda (Farrel Radya Putra)
    data object AgendaList : Screen("agenda_list")
    data object AgendaForm : Screen("agenda_form")
}
