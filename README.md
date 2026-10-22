# 🚀 Managevent - Android Application

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)

**Managevent** adalah aplikasi Android berbasis **Kotlin** dan **Jetpack Compose** untuk pengelolaan, pemesanan, ulasan, wishlist, dan agenda event yang terintegrasi dengan backend **Supabase**.

---

## 📱 Arsitektur Aplikasi

Aplikasi dibangun menggunakan pola **MVVM (Model-View-ViewModel)** dengan struktur folder modular berbasis fitur (*feature-based*):

```text
com.managevent.app
├── core/           # Konfigurasi bersama (SupabaseClient, NavGraph, Theme, Reusable UI)
├── auth/           # Modul Autentikasi (Login, Register, Profile)
└── features/       # Modul Fitur Utama Anggota (Katalog, Pemesanan, Ulasan, Wishlist, Agenda)
```

---

## 👥 Tim & Pembagian Modul

| No | Modul / Fitur | Mahasiswa Penanggung Jawab | Lokasi Modul |
|---|---|---|---|
| 1 | **Katalog Event** | Muhammad Abdul Fatah | `features/katalog/` |
| 2 | **Pemesanan Tiket** | Muhammad Darren Achilles Setiawan | `features/pemesanan/` |
| 3 | **Ulasan & Rating** | Rayhan Wira Pamungkas | `features/ulasan/` |
| 4 | **Wishlist Event** | Muhammad Syauqi Imaduddin | `features/wishlist/` |
| 5 | **Agenda Event** | Farrel Radya Putra | `features/agenda/` |

---

## 📚 Dokumentasi Tim

Dokumentasi lengkap untuk pengembangan proyek dapat diakses di direktori `/docs`:

- 📘 **[Panduan Pengerjaan & Setup Tim](docs/README.md)** — Alur pengerjaan, setup Supabase, dan strategi Git branching.
- 📐 **[Coding Guidelines & Standards](docs/CODING_GUIDELINES.md)** — Standar penulisan kode Kotlin, Jetpack Compose, state management (`UiState`), dan konvensi commit Git.

---

## 🛠️ Cara Menjalankan Proyek

1. Clone repositori ini:
   ```bash
   git clone https://github.com/USERNAME/managevent.git
   ```
2. Buka proyek di **Android Studio Ladybug** (atau versi terbaru).
3. Lakukan **Gradle Sync**.
4. Masukkan `SUPABASE_URL` dan `SUPABASE_KEY` di `com/managevent/app/core/network/SupabaseClient.kt`.
5. Jalankan aplikasi di emulator atau perangkat fisik (min SDK 26).
