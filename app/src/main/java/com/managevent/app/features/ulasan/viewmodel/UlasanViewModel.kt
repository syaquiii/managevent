package com.managevent.app.features.ulasan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.ulasan.data.UlasanModel
import com.managevent.app.features.ulasan.data.UlasanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UlasanViewModel(
    private val repository: UlasanRepository = UlasanRepository()
) : ViewModel() {

    private val _ulasanState = MutableStateFlow<UiState<List<UlasanModel>>>(UiState.Idle)
    val ulasanState: StateFlow<UiState<List<UlasanModel>>> = _ulasanState.asStateFlow()

    // State untuk Filter (0 = Semua, 1-5 = Bintang)
    var selectedRatingFilter by mutableIntStateOf(0)
        private set

    // State untuk Form Input
    var ratingInput by mutableIntStateOf(5)
        private set
    var commentInput by mutableStateOf("")
        private set

    // List asli sebelum difilter
    private var allUlasan: List<UlasanModel> = emptyList()

    fun fetchUlasan(eventId: String) {
        viewModelScope.launch {
            _ulasanState.value = UiState.Loading

            // --- DATA DUMMY UNTUK DEMO FE ---
            kotlinx.coroutines.delay(500)
            allUlasan = listOf(
                UlasanModel(
                    id = "1",
                    username = "Sarah Wijaya",
                    rating = 5,
                    comment = "Pengalaman pertama kali hadir di festival musik ini dan luar biasa rapi. Manajemen antrean toilet dan tempat ibadah dipersiapkan dengan baik. Pasti akan beli tiket lagi tahun depan!"
                ),
                UlasanModel(
                    id = "2",
                    username = "Budi Pratama",
                    rating = 4,
                    comment = "Bintang tamu dan visual panggung spektakuler! Sedikit masukan untuk pendingin udara di area VIP lounge yang sempat kurang dingin pada siang hari, namun selebihnya koordinasi kru sangat responsif."
                ),
                UlasanModel(
                    id = "3",
                    username = "Maya Kusuma",
                    rating = 5,
                    comment = "Pemberitahuan rundown di aplikasi sangat membantu, tidak ketinggalan sesi workshop utama. Kru lapangan ramah dan sigap memandu pengunjung!"
                )
            )
            applyFilter()
        }
    }

    fun onFilterChanged(rating: Int) {
        selectedRatingFilter = rating
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (selectedRatingFilter == 0) {
            allUlasan
        } else {
            allUlasan.filter { it.rating == selectedRatingFilter }
        }
        _ulasanState.value = UiState.Success(filtered)
    }

    fun onRatingChange(newRating: Int) { ratingInput = newRating }
    fun onCommentChange(newComment: String) { commentInput = newComment }

    fun submitUlasan(eventId: String, userId: String, username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Simulasi loading sebentar
            kotlinx.coroutines.delay(1000)

            // Bikin ulasan baru dari inputan form
            val newUlasan = UlasanModel(
                id = java.util.UUID.randomUUID().toString(),
                eventId = eventId,
                userId = userId,
                username = "Kamu", // Nama dummy biar gampang dibedain
                rating = ratingInput,
                comment = commentInput
            )

            // Masukkan ulasan baru ke urutan paling atas
            allUlasan = listOf(newUlasan) + allUlasan
            applyFilter()

            onSuccess() // Balik ke halaman list
        }
    }

    fun deleteUlasan(ulasanId: String, eventId: String) {
        viewModelScope.launch {
            // Simulasi hapus ulasan dari list
            allUlasan = allUlasan.filterNot { it.id == ulasanId }
            applyFilter()
        }
    }
}