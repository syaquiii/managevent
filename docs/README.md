# 📱 Documentation - Managevent Android Application

Selamat datang di repositori proyek **Managevent**. Dokumen ini berisi panduan alur pengerjaan dan struktur arsitektur proyek untuk seluruh anggota tim/mahasiswa.

---

## 📂 Struktur Arsitektur & Paket

Aplikasi dibangun menggunakan **Kotlin**, **Jetpack Compose**, dan **Supabase**, dengan pola arsitektur **MVVM (Model-View-ViewModel)** serta pendekatan modular/feature-based.

```text
com.managevent.app
│
├── core/                           # Konfigurasi utama & komponen global
│   ├── network/                    # Inisialisasi Supabase Client (SupabaseClientProvider)
│   ├── navigation/                 # Rute & NavHost (Screen & AppNavHost)
│   ├── theme/                      # Theme, Color, dan Typography Material 3
│   ├── components/                 # Reusable UI (AppTopBar, AppButton, LoadingScreen)
│   └── util/                       # Helper & Utility State (UiState)
│
├── auth/                           # Modul Autentikasi
│   ├── ui/                         # LoginScreen, RegisterScreen, ProfileScreen
│   ├── viewmodel/                  # AuthViewModel
│   └── data/                       # UserModel & AuthRepository
│
├── features/                       # Modul Fitur Utama Anggota (Independen)
│   │
│   ├── katalog/                    # Fitur 1: Muhammad Abdul Fatah
│   │   ├── ui/                     # KatalogListScreen, KatalogDetailScreen, KatalogFormScreen
│   │   ├── viewmodel/              # KatalogViewModel
│   │   └── data/                   # EventModel & KatalogRepository
│   │
│   ├── pemesanan/                  # Fitur 2: Muhammad Darren Achilles Setiawan
│   │   ├── ui/                     # CheckoutScreen, RiwayatPemesananScreen, DetailPemesananScreen
│   │   ├── viewmodel/              # PemesananViewModel
│   │   └── data/                   # TransaksiModel & PemesananRepository
│   │
│   ├── ulasan/                     # Fitur 3: Rayhan Wira Pamungkas
│   │   ├── ui/                     # UlasanListScreen, UlasanFormScreen
│   │   ├── viewmodel/              # UlasanViewModel
│   │   └── data/                   # UlasanModel & UlasanRepository
│   │
│   ├── wishlist/                   # Fitur 4: Muhammad Syauqi Imaduddin
│   │   ├── ui/                     # WishlistScreen (LazyVerticalGrid), WishlistDetailScreen
│   │   ├── viewmodel/              # WishlistViewModel
│   │   └── data/                   # WishlistModel & WishlistRepository
│   │
│   └── agenda/                     # Fitur 5: Farrel Radya Putra
│       ├── ui/                     # AgendaListScreen, AgendaFormScreen
│       ├── viewmodel/              # AgendaViewModel
│       └── data/                   # AgendaModel & AgendaRepository
│
└── MainActivity.kt                 # Entry Point Aplikasi
```

---

## 👥 Pembagian Tugas Anggota Kelompok

Setiap mahasiswa bertanggung jawab penuh atas sub-folder di `com.managevent.app.features.<modul>`:

| No | Modul / Fitur | Mahasiswa Penanggung Jawab | Sub-folder Utama |
|---|---|---|---|
| 1 | **Katalog Event** | Muhammad Abdul Fatah | `features/katalog/` |
| 2 | **Pemesanan Tiket** | Muhammad Darren Achilles Setiawan | `features/pemesanan/` |
| 3 | **Ulasan & Rating** | Rayhan Wira Pamungkas | `features/ulasan/` |
| 4 | **Wishlist Event** | Muhammad Syauqi Imaduddin | `features/wishlist/` |
| 5 | **Agenda Event** | Farrel Radya Putra | `features/agenda/` |

---

## 🚀 Cara Memulai (Quick Start)

1. **Clone Repositori & Buka di Android Studio**:
   - Pastikan Android Studio Anda sudah terkonfigurasi dengan JDK 11 atau JDK 17.
   - Lakukan Gradle Sync (`Sync Project with Gradle Files`).

2. **Konfigurasi Supabase Environment Variables**:
   - Buka file `local.properties` di root proyek.
   - Tambahkan baris berikut (sesuaikan dengan credentials Supabase Anda):
     ```properties
     SUPABASE_URL=https://eogkrlvobpbqcloiozuj.supabase.co
     SUPABASE_KEY=sb_publishable_jJSLuqMfueuQitDcIrr2Sw_Gh3XGbt_
     ```
   - Gradle akan membaca variabel ini secara aman melalui `BuildConfig.SUPABASE_URL` dan `BuildConfig.SUPABASE_KEY` tanpa mengekspos kunci di repositori Git.

3. **Menjalankan Aplikasi**:
   - Pilih emulator/perangkat fisik Android dengan minimum SDK 26 (Android 8.0).
   - Klik `Run 'app'`.

---

## 🛠️ Panduan Alur Kerja Tim (Workflow)

1. **Aturan Isolasi Modul**:
   - Kerjakan file **hanya pada paket fitur yang ditugaskan** pada Anda (contoh: Syauqi mengerjakan di `com.managevent.app.features.wishlist`).
   - Jangan mengubah file modul anggota lain secara langsung tanpa koordinasi.

2. **Penambahan Rute Navigasi Baru**:
   - Jika membuat layar baru, daftarkan rutenya di `core/navigation/Screen.kt`.
   - Sambungkan layar baru ke `core/navigation/NavGraph.kt`.

3. **Strategi Git Branching**:
   - Buat branch baru dari `main` dengan format: `feature/nama-fitur` (misal: `feature/katalog-fatah` atau `feature/wishlist-syauqi`).
   - Commit perubahan secara teratur.
   - Buat Pull Request (PR) ke `main` setelah fitur teruji.

---

## 📖 Panduan Penulisan Kode
Silakan baca panduan lengkap penulisan kode di [CODING_GUIDELINES.md](CODING_GUIDELINES.md).
