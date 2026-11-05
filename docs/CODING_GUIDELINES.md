# 📐 Coding Guidelines & Standar Arsitektur (MVVM + UDF + ViewModel Hoisting)

Dokumen ini adalah aturan arsitektur wajib yang harus diikuti saat menulis kode di proyek **Managevent**. Berlaku untuk seluruh anggota tim dan semua modul fitur.

---

## 1. Lapisan & Tanggung Jawab (MVVM + NavHost ViewModel Hoisting)

| Lapisan | Isi | Tugas Utama | Dilarang |
|---|---|---|---|
| **Model** | `data class`, `Repository`, Supabase Client | Menyediakan & mengolah data | Mengenal UI, Context, atau Composable |
| **View (UI)** | Fungsi `@Composable` (Screen, Content) | Menampilkan state, meneruskan event user via callback | Mengambil data langsung, menyimpan ViewModel di Screen |
| **ViewModel** | Turunan `androidx.lifecycle.ViewModel` | Menyimpan UI State, menerima event, memanggil Repository | Disimpan di dalam Screen Composable |
| **NavHost (`NavGraph.kt`)** | `AppNavHost` & Rute Navigasi | Menginstansiasi ViewModel, membaca state, menghubungkan Screen | Menyimpan logika bisnis |

---

## 2. Unidirectional Data Flow (UDF)

- **State mengalir TURUN**: `NavGraph.kt` (ViewModel State) -> `XxxScreen` -> `XxxContent` (UI).
- **Event mengalir NAIK**: User click -> `XxxContent` (lambda) -> `XxxScreen` (lambda) -> `NavGraph.kt` (ViewModel Action).

---

## 3. Aturan Screen & Content (State Hoisting & ViewModel in NavHost)

> 🚨 **DILARANG MENGINSTANSIASI ATAU MENGANAMKAN VIEWMODEL DI DALAM SCREEN**
>
> 1. `XxxScreen` maupun `XxxContent` **TIDAK BOLEH** menerima atau menginstansiasi `ViewModel` (misal: `viewModel: XxxViewModel = viewModel()`).
> 2. Seluruh `ViewModel` diinstansiasi di tingkat **`NavGraph.kt` (`AppNavHost`)**.
> 3. Observasi state `collectAsStateWithLifecycle()` dilakukan di **`NavGraph.kt`**.
> 4. `XxxScreen` dan `XxxContent` hanya menerima data state murni (`UiState`, String, Int, List) dan callback lambda event (`onEvent: () -> Unit`).

```kotlin
// 1. Contoh di NavGraph.kt (ViewModel Hoisted di NavHost)
composable(Screen.KatalogList.route) {
    val viewModel: KatalogViewModel = viewModel()
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    KatalogListScreen(
        eventsState = eventsState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        onEventClick = { id -> navController.navigate(Screen.KatalogDetail.createRoute(id)) },
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onFavoriteToggle = viewModel::toggleFavorite
    )
}

// 2. Contoh di KatalogListScreen.kt (Pure Data & Stateless Screen)
@Composable
fun KatalogListScreen(
    eventsState: UiState<List<EventModel>>,
    searchQuery: String,
    selectedCategory: String,
    onEventClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    KatalogListContent(
        eventsState = eventsState,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        onEventClick = onEventClick,
        onSearchQueryChange = onSearchQueryChange,
        onCategorySelected = onCategorySelected,
        onFavoriteToggle = onFavoriteToggle,
        modifier = modifier
    )
}
```

---

## 4. UI State & StateFlow

1. Gunakan **`UiState<T>`** (`Idle`, `Loading`, `Success`, `Error`) atau `data class` UI State per layar.
2. ViewModel mengekspos state sebagai `StateFlow` read-only:
   ```kotlin
   private val _uiState = MutableStateFlow<UiState<List<EventModel>>>(UiState.Idle)
   val uiState: StateFlow<UiState<List<EventModel>>> = _uiState.asStateFlow()
   ```
3. Update `MutableStateFlow` menggunakan `.update { it.copy(...) }`.

---

## 5. Penanganan Coroutine & Error Handling

1. Jalankan coroutine di `viewModelScope.launch`.
2. Bungkus operasi asynchronous dalam `try-catch`.
3. **Selalu lempar ulang `CancellationException`** agar Coroutine Lifecycle tidak terganggu:
   ```kotlin
   viewModelScope.launch {
       _uiState.update { UiState.Loading }
       try {
           val data = repository.getData()
           _uiState.update { UiState.Success(data) }
       } catch (e: CancellationException) {
           throw e
       } catch (e: Exception) {
           _uiState.update { UiState.Error(e.localizedMessage ?: "Terjadi kesalahan") }
       }
   }
   ```

---

## 6. Checklist Sebelum Commit / PR

- [ ] **Tidak ada `viewModel()` atau `ViewModel` di dalam file `Screen` / `Content`**.
- [ ] ViewModel diinstansiasi dan dibaca di `NavGraph.kt`.
- [ ] Screen & Content dipisah, Content stateless dan memiliki `@Preview`.
- [ ] `CancellationException` dilempar ulang pada `catch` block di ViewModel.
- [ ] Menambahkan `key` pada item List (`LazyColumn` / `LazyVerticalGrid`).
