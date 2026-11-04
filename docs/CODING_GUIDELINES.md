# 📐 Coding Guidelines & Standar Arsitektur (MVVM + UDF)

Dokumen ini adalah aturan arsitektur wajib yang harus diikuti saat menulis kode di proyek **Managevent**. Berlaku untuk seluruh anggota tim dan semua modul fitur.

---

## 1. Lapisan & Tanggung Jawab (MVVM)

| Lapisan | Isi | Tugas Utama | Dilarang |
|---|---|---|---|
| **Model** | `data class`, `Repository`, Supabase Client | Menyediakan & mengolah data | Mengenal UI, Context, atau Composable |
| **View (UI)** | Fungsi `@Composable` (Screen, Content) | Menampilkan state, meneruskan event user ke ViewModel | Mengambil data langsung, menyimpan logika bisnis |
| **ViewModel** | Turunan `androidx.lifecycle.ViewModel` | Menyimpan UI State, menerima event, memanggil Repository | Menyimpan referensi `Context`, `Activity`, atau Composable |

---

## 2. Unidirectional Data Flow (UDF)

- **State mengalir TURUN**: ViewModel -> `XxxScreen` -> `XxxContent` (UI).
- **Event mengalir NAIK**: User click -> `XxxContent` (lambda) -> `XxxScreen` -> ViewModel.

### Aturan Utama UDF:
1. State **hanya boleh diubah** di ViewModel.
2. View **hanya membaca state** dan mengirim event via callback/lambda.
3. Objek List harus diperbarui dengan **objek/list baru** (`list + item`, `filterNot`), jangan mutasi `list.add()`.

---

## 3. Aturan Screen & Content (State Hoisting)

Setiap layar wajib dipisah menjadi 2 Composable function:

1. **`XxxScreen` (Stateful)**:
   - Memegang `ViewModel`.
   - Mengamati state dengan `collectAsStateWithLifecycle()`.
   - Meneruskan data state & lambda event ke `XxxContent`.
2. **`XxxContent` (Stateless)**:
   - **Hanya menerima nilai/data biasa** dan lambda event.
   - Tidak memegang `ViewModel` atau memanggil Repository.
   - Memiliki parameter wajib `modifier: Modifier = Modifier`.
   - **Wajib memiliki `@Preview`** dengan data dummy.

```kotlin
// Stateful Screen
@Composable
fun KatalogListScreen(
    modifier: Modifier = Modifier,
    viewModel: KatalogViewModel = viewModel(),
    onEventClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KatalogListContent(
        uiState = uiState,
        onEventClick = onEventClick,
        modifier = modifier
    )
}

// Stateless Content
@Composable
fun KatalogListContent(
    uiState: UiState<List<EventModel>>,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Render UI murni berdasarkan uiState
}

// Preview dengan Dummy Data
@Preview(showBackground = true)
@Composable
fun KatalogListContentPreview() {
    ManageventTheme {
        KatalogListContent(
            uiState = UiState.Success(listOf(/* dummy events */)),
            onEventClick = {}
        )
    }
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

### Pola Input Form (TextField) di ViewModel
Untuk teks input form yang divalidasi/dikirim ke server:
```kotlin
var emailInput by mutableStateOf("")
    private set

fun onEmailChange(newValue: String) {
    emailInput = newValue
}
```

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

- [ ] Screen & Content dipisah, Content stateless dan memiliki `@Preview`.
- [ ] State dibaca dengan `collectAsStateWithLifecycle()`.
- [ ] Tidak ada pemanggilan Repository / Retrofit / Supabase di dalam Composable.
- [ ] ViewModel tidak menyimpan `Context`, `Activity`, atau Composable.
- [ ] `CancellationException` dilempar ulang pada `catch` block.
- [ ] Menambahkan `key` pada item List (`LazyColumn` / `LazyVerticalGrid`).
