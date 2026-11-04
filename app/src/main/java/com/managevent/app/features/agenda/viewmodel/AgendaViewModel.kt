package com.managevent.app.features.agenda.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.agenda.data.AgendaModel
import com.managevent.app.features.agenda.data.AgendaRepository
import com.managevent.app.features.agenda.data.BookedEventModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AgendaViewModel(
    private val repository: AgendaRepository = AgendaRepository()
) : ViewModel() {

    // ---------- Layar 1: Event dipesan ----------
    private val _bookedState =
        MutableStateFlow<UiState<List<BookedEventModel>>>(UiState.Idle)
    val bookedState: StateFlow<UiState<List<BookedEventModel>>> = _bookedState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    fun onSearchChange(value: String) { searchQuery = value }

    fun loadBookedEvents() = launchLoading(_bookedState) { repository.getBookedEvents() }

    // ---------- Layar 2: Daftar catatan ----------
    private val _notesState = MutableStateFlow<UiState<List<AgendaModel>>>(UiState.Idle)
    val notesState: StateFlow<UiState<List<AgendaModel>>> = _notesState.asStateFlow()

    var currentEventId: String? = null
        private set

    private val _message = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val message: SharedFlow<String> = _message.asSharedFlow()   // untuk Snackbar

    fun loadNotes(eventId: String) {
        currentEventId = eventId
        launchLoading(_notesState) { repository.getNotes(eventId) }
    }

    fun toggleDone(note: AgendaModel) {
        val id = note.id ?: return
        viewModelScope.launch {
            try {
                repository.setDone(id, !note.isDone)
                reloadNotes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.localizedMessage ?: "Gagal memperbarui catatan")
            }
        }
    }

    fun deleteNote(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.deleteNote(id)
                _message.emit("Catatan dihapus")
                reloadNotes()
                onDone()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.localizedMessage ?: "Gagal menghapus catatan")
            }
        }
    }

    fun deleteCurrentNote(onDone: () -> Unit = {}) {
        val id = editingNote?.id
        if (id != null) {
            deleteNote(id, onDone)
        } else {
            onDone()
        }
    }

    // ---------- Layar 3: Form ----------
    var titleInput by mutableStateOf("")
        private set
    var notesInput by mutableStateOf("")
        private set
    var dateInput by mutableStateOf("")          // ISO "2026-12-20"
        private set
    var isDoneInput by mutableStateOf(false)
        private set
    var titleError by mutableStateOf<String?>(null)
        private set
    var dateError by mutableStateOf<String?>(null)
        private set
    var isSaving by mutableStateOf(false)
        private set

    private var editingNote: AgendaModel? = null
    val isEditMode: Boolean get() = editingNote != null

    fun onTitleChange(v: String) { titleInput = v; titleError = null }
    fun onNotesChange(v: String) { notesInput = v }
    fun onDateChange(v: String) { dateInput = v; dateError = null }
    fun onDoneChange(v: Boolean) { isDoneInput = v }

    fun startAdd() {
        editingNote = null
        titleInput = ""; notesInput = ""; dateInput = ""; isDoneInput = false
        titleError = null; dateError = null
    }

    fun startEdit(note: AgendaModel) {
        editingNote = note
        titleInput = note.title
        notesInput = note.notes.orEmpty()
        dateInput = note.date
        isDoneInput = note.isDone
        titleError = null; dateError = null
    }

    fun save(onSuccess: () -> Unit) {
        titleError = if (titleInput.isBlank()) "Judul wajib diisi" else null
        dateError = if (dateInput.isBlank()) "Target selesai wajib diisi" else null
        if (titleError != null || dateError != null) return

        val eventId = currentEventId ?: return
        val base = editingNote
        val note = AgendaModel(
            id = base?.id,
            eventId = eventId,
            title = titleInput.trim(),
            date = dateInput,
            notes = notesInput.trim().ifBlank { null },
            isDone = isDoneInput
        )

        viewModelScope.launch {
            isSaving = true
            try {
                if (base == null) repository.addNote(note) else repository.updateNote(note)
                _message.emit(if (base == null) "Catatan ditambahkan" else "Catatan diperbarui")
                reloadNotes()
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.localizedMessage ?: "Gagal menyimpan catatan")
            } finally {
                isSaving = false
            }
        }
    }

    // ---------- Helper ----------
    private fun reloadNotes() { currentEventId?.let { loadNotes(it) } }

    private fun <T> launchLoading(
        state: MutableStateFlow<UiState<T>>,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            state.update { UiState.Loading }
            try {
                state.update { UiState.Success(block()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                state.update { UiState.Error(e.localizedMessage ?: "Terjadi kesalahan") }
            }
        }
    }
}