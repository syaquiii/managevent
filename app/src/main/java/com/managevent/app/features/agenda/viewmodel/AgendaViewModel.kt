package com.managevent.app.features.agenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.core.util.UiState
import com.managevent.app.features.agenda.data.AgendaModel
import com.managevent.app.features.agenda.data.AgendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgendaViewModel(
    private val repository: AgendaRepository = AgendaRepository()
) : ViewModel() {

    private val _agendaState = MutableStateFlow<UiState<List<AgendaModel>>>(UiState.Idle)
    val agendaState: StateFlow<UiState<List<AgendaModel>>> = _agendaState.asStateFlow()

    fun fetchAgenda(userId: String, date: String) {
        viewModelScope.launch {
            _agendaState.value = UiState.Loading
            _agendaState.value = repository.getAgendaByDate(userId, date)
        }
    }
}
