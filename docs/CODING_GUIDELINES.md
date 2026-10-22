# 📐 Coding Guidelines - Managevent Android Application

Dokumen ini menjelaskan standar penulisan kode, konvensi nama, dan praktik terbaik (*best practices*) yang wajib diikuti oleh seluruh anggota tim proyek **Managevent**.

---

## 1. Konvensi Penamaan (Naming Conventions)

### 📄 Class, Interface, & Composable Functions
Gunakan **PascalCase**:
- **Class / Model**: `UserModel`, `EventModel`, `TransaksiModel`
- **ViewModel**: `AuthViewModel`, `KatalogViewModel`, `WishlistViewModel`
- **Repository**: `AuthRepository`, `KatalogRepository`, `WishlistRepository`
- **Composable Function / Screen**: `LoginScreen`, `KatalogListScreen`, `AppButton`

### Variables & Functions
Gunakan **camelCase**:
- **Variabel / Property**: `userId`, `eventsState`, `totalHarga`
- **Fungsi / Event**: `fetchEvents()`, `onEventClick()`, `addToWishlist()`

### 📦 Packages
Gunakan **lowercase** tanpa pemisah:
- `com.managevent.app.core.network`
- `com.managevent.app.features.katalog.ui`

---

## 2. Pola Arsitektur MVVM & State Management

Setiap modul fitur mengikuti pola 3 layer: `Data (Model & Repository) -> ViewModel -> UI (Compose)`.

### Layer Data (Repository)
Panggilan Supabase dilakukan di `Dispatchers.IO` dan selalu mengembalikan status dengan wrapper `UiState<T>`:

```kotlin
class KatalogRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getEvents(): UiState<List<EventModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = client.postgrest["events"].select().decodeList<EventModel>()
                UiState.Success(events)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil data event")
            }
        }
    }
}
```

### Layer ViewModel
ViewModel mengelola UI state menggunakan `MutableStateFlow` (private) dan mengekspos `StateFlow` (public/read-only):

```kotlin
class KatalogViewModel(
    private val repository: KatalogRepository = KatalogRepository()
) : ViewModel() {

    private val _eventsState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
    val eventsState: StateFlow<UiState<List<EventModel>>> = _eventsState.asStateFlow()

    fun fetchEvents() {
        viewModelScope.launch {
            _eventsState.value = UiState.Loading
            _eventsState.value = repository.getEvents()
        }
    }
}
```

### Layer UI (Jetpack Compose)
Observe state menggunakan `collectAsStateWithLifecycle()` dan tangani keempat kondisi `UiState`:

```kotlin
@Composable
fun KatalogListScreen(
    viewModel: KatalogViewModel = viewModel()
) {
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()

    when (val state = eventsState) {
        is UiState.Idle -> { /* Tampilan awal sebelum load */ }
        is UiState.Loading -> { LoadingScreen() }
        is UiState.Success -> {
            val eventList = state.data
            // Tampilkan daftar event menggunakan LazyColumn/LazyVerticalGrid
        }
        is UiState.Error -> {
            // Tampilkan pesan error & tombol coba lagi
        }
    }
}
```

---

## 3. Pedoman Jetpack Compose UI

1. **State Hoisting**:
   - Pisahkan logika state dari UI murni.
   - Buat fungsi Composable menerima data & callback event (misal: `onEventClick: (String) -> Unit`).
2. **Penggunaan Component Global**:
   - Gunakan komponen siap pakai yang ada di `core/components/` seperti `AppTopBar`, `AppButton`, dan `LoadingScreen`.
3. **Material 3 Theme**:
   - Hindari warna *hardcoded*. Gunakan `MaterialTheme.colorScheme.primary`, `MaterialTheme.typography.bodyLarge`, dll.

---

## 4. Konvensi Commit Message Git

Gunakan format commit yang jelas untuk mempermudah tracking:

- `feat(katalog): tambahkan form tambah event baru`
- `fix(auth): perbaiki error validasi password`
- `docs(readme): perbarui panduan setup proyek`
- `refactor(core): sederhanakan rute navigasi`
