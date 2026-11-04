# 📱 Documentation - Managevent Android Application

Selamat datang di repositori proyek **Managevent** (Pemrograman Aplikasi Mobile - Proyek Akhir V2). Dokumen ini berisi panduan alur pengerjaan dan rincian persyaratan fitur (*REQUIREMENTS*) untuk seluruh anggota kelompok.

---

## 🔐 Peran Pengguna (RBAC)

1. **Vendor**: Menambah, mengubah, dan menghapus event/paket yang ditawarkan (+ upload poster).
2. **Pemesan**: Melihat event, memesan paket, memberi ulasan, menyimpan favorit, dan mencatat persiapan event.

---

## 👥 Pembagian & Rincian Requirements Fitur

Setiap anggota bertanggung jawab penuh atas modul fitur independen di `com.managevent.app.features.<modul>`:

### 1. Katalog Event & Paket (`features/katalog/`) — *Muhammad Abdul Fatah*
- **Tampilan List**: `LazyVerticalGrid` (kartu event dengan poster).
- **Hak Akses**: Vendor (CRUD + Upload poster), Pemesan (Read list & detail).
- **Layar Navigasi**:
  1. `KatalogListScreen` (grid event & FAB tambah khusus Vendor)
  2. `KatalogDetailScreen` (poster, deskripsi, lokasi, harga)
  3. `KatalogFormScreen` (form input/edit & upload poster)
- **Supabase CRUD**: Read, Insert (+ Upload ke Supabase Storage `posters`), Update, Delete.

---

### 2. Pemesanan & Riwayat Transaksi (`features/pemesanan/`) — *Muhammad Darren Achilles Setiawan*
- **Tampilan List**: `HorizontalPager` (ViewPager tab "Berjalan" & "Selesai") + `LazyColumn`.
- **Layar Navigasi**:
  1. `CheckoutScreen` (form checkout: pilih paket, tanggal, jumlah peserta, catatan)
  2. `RiwayatPemesananScreen` (`HorizontalPager` tab Berjalan/Selesai)
  3. `DetailPemesananScreen` (rincian pesanan, opsi pembatalan/update status)
- **Supabase CRUD**: Read, Insert (checkout), Update (status), Delete (batal).

---

### 3. Ulasan, Rating & Komentar (`features/ulasan/`) — *Rayhan Wira Pamungkas*
- **Tampilan List**: `LazyRow` (filter rating 1-5★) + `LazyColumn` (daftar ulasan).
- **Layar Navigasi**:
  1. `UlasanListScreen` (filter & list ulasan)
  2. `UlasanFormScreen` (input rating bintang & teks testimoni)
- **Supabase CRUD**: Read, Insert, Update, Delete (ulasan milik sendiri).

---

### 4. Wishlist / Favorit (`features/wishlist/`) — *Muhammad Syauqi Imaduddin*
- **Tampilan List**: `LazyVerticalGrid` (grid event favorit).
- **Layar Navigasi**:
  1. `WishlistScreen` (grid event tersimpan)
  2. `WishlistDetailScreen` (detail event & pintasan ke checkout/hapus)
- **Supabase CRUD**: Read, Insert (tambah), Delete (hapus).

---

### 5. Catatan Persiapan Event (`features/agenda/`) — *Farrel Radya Putra*
- **Hak Akses**: Pemesan, khusus untuk event yang sudah dipesan.
- **Tampilan List**: `LazyColumn` (daftar event dipesan & catatan per event).
- **Layar Navigasi**:
  1. `AgendaListScreen` (daftar event dipesan & catatan persiapan)
  2. `AgendaFormScreen` (form input/edit catatan persiapan)
- **Supabase CRUD**: Read, Insert, Update, Delete.

---

## 🛠️ Ringkasan Komponen Teknis

| Fitur | Komponen List | Navigasi Screen | Supabase CRUD |
|---|---|---|---|
| **1. Katalog** | `LazyVerticalGrid` | List -> Detail -> Form | Read, Insert (+ Storage), Update, Delete |
| **2. Pemesanan** | `HorizontalPager` + `LazyColumn` | Checkout -> Riwayat -> Detail | Read, Insert, Update, Delete |
| **3. Ulasan** | `LazyRow` + `LazyColumn` | List -> Form | Read, Insert, Update, Delete |
| **4. Wishlist** | `LazyVerticalGrid` | List -> Detail | Read, Insert, Delete |
| **5. Agenda** | `LazyColumn` | Event Dipesan -> Catatan -> Form | Read, Insert, Update, Delete |

---

## 🚀 Cara Memulai (Quick Start)

1. Clone repositori & lakukan **Gradle Sync**.
2. Isikan credentials Supabase di `local.properties`:
   ```properties
   SUPABASE_URL=https://eogkrlvobpbqcloiozuj.supabase.co
   SUPABASE_KEY=sb_publishable_jJSLuqMfueuQitDcIrr2Sw_Gh3XGbt_
   ```
3. Jalankan script database **[docs/supabase_schema.sql](supabase_schema.sql)** di SQL Editor Dashboard Supabase Anda.
