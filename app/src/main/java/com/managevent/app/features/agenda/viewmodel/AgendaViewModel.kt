package com.managevent.app.features.agenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
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
    private val _bookedState = MutableStateFlow<UiState<List<BookedEventModel>>>(UiState.Idle)
    val bookedState: StateFlow<UiState<List<BookedEventModel>>> = _bookedState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchChange(value: String) {
        _searchQuery.update { value }
    }

    fun loadBookedEvents() {
        viewModelScope.launch {
            _bookedState.update { UiState.Loading }
            try {
                _bookedState.update { repository.getBookedEvents() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _bookedState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    // ---------- Layar 2: Daftar catatan ----------
    private val _notesState = MutableStateFlow<UiState<List<AgendaModel>>>(UiState.Idle)
    val notesState: StateFlow<UiState<List<AgendaModel>>> = _notesState.asStateFlow()

    var currentEventId: String? = null
        private set

    private val _currentBookedEvent = MutableStateFlow<BookedEventModel?>(null)
    val currentBookedEvent: StateFlow<BookedEventModel?> = _currentBookedEvent.asStateFlow()

    private val _message = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val message: SharedFlow<String> = _message.asSharedFlow()

    fun loadNotes(eventId: String) {
        currentEventId = eventId
        val cachedBooked = (_bookedState.value as? UiState.Success)?.data?.firstOrNull { it.eventId == eventId }
        if (cachedBooked != null) {
            _currentBookedEvent.update { cachedBooked }
        } else {
            _currentBookedEvent.update { null }
            viewModelScope.launch {
                try {
                    val bookedResult = repository.getBookedEvents()
                    if (bookedResult is UiState.Success) {
                        _currentBookedEvent.update { bookedResult.data.firstOrNull { it.eventId == eventId } }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {}
            }
        }

        viewModelScope.launch {
            _notesState.update { UiState.Loading }
            try {
                _notesState.update { repository.getNotes(eventId) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _notesState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun toggleDone(note: AgendaModel) {
        val id = note.id ?: return
        viewModelScope.launch {
            try {
                when (val result = repository.setDone(id, !note.isDone)) {
                    is UiState.Success -> reloadNotes()
                    is UiState.Error -> _message.emit(result.message)
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.toUserFriendlyMessage())
            }
        }
    }

    fun deleteNote(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                when (val result = repository.deleteNote(id)) {
                    is UiState.Success -> {
                        _message.emit(result.data)
                        reloadNotes()
                        onDone()
                    }
                    is UiState.Error -> _message.emit(result.message)
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.toUserFriendlyMessage())
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
    private val _titleInput = MutableStateFlow("")
    val titleInput: StateFlow<String> = _titleInput.asStateFlow()

    private val _notesInput = MutableStateFlow("")
    val notesInput: StateFlow<String> = _notesInput.asStateFlow()

    private val _dateInput = MutableStateFlow("")
    val dateInput: StateFlow<String> = _dateInput.asStateFlow()

    private val _isDoneInput = MutableStateFlow(false)
    val isDoneInput: StateFlow<Boolean> = _isDoneInput.asStateFlow()

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var editingNote: AgendaModel? = null
    val isEditMode: Boolean get() = editingNote != null

    fun onTitleChange(v: String) {
        _titleInput.update { v }
        _titleError.update { null }
    }

    fun onNotesChange(v: String) {
        _notesInput.update { v }
    }

    fun onDateChange(v: String) {
        _dateInput.update { v }
        _dateError.update { null }
    }

    fun onDoneChange(v: Boolean) {
        _isDoneInput.update { v }
    }

    fun startAdd(eventId: String? = null) {
        if (!eventId.isNullOrBlank()) currentEventId = eventId
        editingNote = null
        _titleInput.update { "" }
        _notesInput.update { "" }
        _dateInput.update { "" }
        _isDoneInput.update { false }
        _titleError.update { null }
        _dateError.update { null }
    }

    fun startEdit(note: AgendaModel, eventId: String? = null) {
        if (!eventId.isNullOrBlank()) currentEventId = eventId
        editingNote = note
        _titleInput.update { note.title }
        _notesInput.update { note.notes.orEmpty() }
        _dateInput.update { note.date }
        _isDoneInput.update { note.isDone }
        _titleError.update { null }
        _dateError.update { null }
    }

    fun save(eventIdArg: String = "", onSuccess: () -> Unit) {
        val currentTitle = _titleInput.value
        val currentDate = _dateInput.value
        _titleError.update { if (currentTitle.isBlank()) "Judul wajib diisi" else null }
        _dateError.update { if (currentDate.isBlank()) "Target selesai wajib diisi" else null }
        if (_titleError.value != null || _dateError.value != null) return

        val targetEventId = eventIdArg.ifBlank { currentEventId.orEmpty() }
        if (targetEventId.isBlank()) {
            _message.tryEmit("Event tidak ditemukan.")
            return
        }
        currentEventId = targetEventId

        val base = editingNote
        val note = AgendaModel(
            id = base?.id,
            eventId = targetEventId,
            title = currentTitle.trim(),
            date = currentDate,
            notes = _notesInput.value.trim().ifBlank { null },
            isDone = _isDoneInput.value
        )

        viewModelScope.launch {
            _isSaving.update { true }
            try {
                val result = if (base == null) repository.addNote(note) else repository.updateNote(note)
                when (result) {
                    is UiState.Success -> {
                        _message.emit(result.data)
                        reloadNotes()
                        onSuccess()
                    }
                    is UiState.Error -> {
                        _message.emit(result.message)
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.emit(e.toUserFriendlyMessage())
            } finally {
                _isSaving.update { false }
            }
        }
    }

    private fun reloadNotes() {
        currentEventId?.let { loadNotes(it) }
    }
}
