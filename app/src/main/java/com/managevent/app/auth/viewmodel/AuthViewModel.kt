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

    fun register(email: String, pass: String, fullName: String = "", role: String = "PEMESAN") {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = repository.register(email, pass, fullName, role)
                if (result is UiState.Success) {
                    _loginState.value = UiState.Success(UserModel(email = email, fullName = fullName, role = role))
                } else if (result is UiState.Error) {
                    _loginState.value = UiState.Error(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.localizedMessage ?: "Registrasi gagal")
            }
        }
    }
}
