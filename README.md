# 🚀 Managevent - Android Application

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)

**Managevent** adalah platform manajemen event mobile berbasis **Kotlin**, **Jetpack Compose**, dan **Supabase**. Pengguna dapat mencari event, memesan paket, memberi ulasan, menyimpan favorit, dan mencatat persiapan event.

---

## 🔐 Peran Pengguna (RBAC)

- **Vendor**: Menambah, mengubah, dan menghapus event/paket (+ upload poster).
- **Pemesan**: Melihat event, memesan paket, memberi ulasan, menyimpan wishlist, dan mencatat persiapan event.

---

## 👥 Tim & Pembagian Modul Fitur

| No | Modul / Fitur | Mahasiswa Penanggung Jawab | Tampilan List | Operations |
|---|---|---|---|---|
| 1 | **Katalog Event & Paket** | Muhammad Abdul Fatah | `LazyVerticalGrid` | Read, Insert (+ Storage), Update, Delete |
| 2 | **Pemesanan & Riwayat Transaksi** | Muhammad Darren Achilles Setiawan | `HorizontalPager` + `LazyColumn` | Read, Insert, Update, Delete |
| 3 | **Ulasan & Rating** | Rayhan Wira Pamungkas | `LazyRow` + `LazyColumn` | Read, Insert, Update, Delete |
| 4 | **Wishlist / Favorit** | Muhammad Syauqi Imaduddin | `LazyVerticalGrid` | Read, Insert, Delete |
| 5 | **Catatan Persiapan Event** | Farrel Radya Putra | `LazyColumn` | Read, Insert, Update, Delete |

---

## 📚 Dokumentasi Lengkap

- 📘 **[Panduan Requirements & Setup Tim](docs/README.md)** — Rincian teknis, RBAC, dan alur Supabase CRUD.
- 📐 **[Coding Guidelines & Standards](docs/CODING_GUIDELINES.md)** — Standar arsitektur MVVM + UDF, `UiState`, State Hoisting, dan Coroutines.
- 🗄️ **[Supabase Schema SQL](docs/supabase_schema.sql)** — Script pembuat tabel & kebijakan RLS untuk Supabase Dashboard.

---

## 🛠️ Cara Menjalankan Proyek

1. Clone repositori ini:
   ```bash
   git clone https://github.com/USERNAME/managevent.git
   ```
2. Buka proyek di **Android Studio**.
3. Buka file `local.properties` di root folder, lalu tambahkan credentials Supabase:
   ```properties
   SUPABASE_URL=https://eogkrlvobpbqcloiozuj.supabase.co
   SUPABASE_KEY=sb_publishable_jJSLuqMfueuQitDcIrr2Sw_Gh3XGbt_
   ```
4. Lakukan **Gradle Sync** & **Run 'app'**.
