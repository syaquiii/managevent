package com.managevent.app.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.auth.data.AuthRepository
import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.util.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserModel>> = _loginState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                _loginState.value = repository.login(email, pass)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }
}
