package com.managevent.app.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.auth.data.AuthRepository
import com.managevent.app.auth.data.RegisterResult
import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserModel>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<RegisterResult>>(UiState.Idle)
    val registerState: StateFlow<UiState<RegisterResult>> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val actionState: StateFlow<UiState<String>> = _actionState.asStateFlow()

    init {
        checkCurrentSession()
    }

    fun resetAuthState() {
        _loginState.update { UiState.Idle }
        _registerState.update { UiState.Idle }
        _actionState.update { UiState.Idle }
    }

    fun clearActionState() {
        _actionState.update { UiState.Idle }
    }

    fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val profile = repository.fetchCurrentProfile()
                _currentUser.update { profile }
                if (profile != null) {
                    _loginState.update { UiState.Success(profile) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Keep session idle
            }
        }
    }

    fun updateProfile(fullName: String, role: String) {
        val uid = _currentUser.value?.id.orEmpty()
        viewModelScope.launch {
            _actionState.update { UiState.Loading }
            try {
                when (val result = repository.updateProfile(uid, fullName, role)) {
                    is UiState.Success -> {
                        _currentUser.update { result.data }
                        _actionState.update { UiState.Success("Profil berhasil diperbarui.") }
                    }
                    is UiState.Error -> {
                        _actionState.update { UiState.Error(result.message) }
                    }
                    else -> {}
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _actionState.update { UiState.Loading }
            try {
                _actionState.update { repository.updatePassword(newPassword) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _actionState.update { UiState.Loading }
            try {
                _actionState.update { repository.sendPasswordReset(email) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actionState.update { UiState.Error(e.toUserFriendlyMessage()) }
            }
        }
    }

    fun login(
        email: String,
        pass: String,
        onSuccess: (UserModel) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _loginState.update { UiState.Loading }
            try {
                val result = repository.login(email, pass)
                _loginState.update { result }
                if (result is UiState.Success) {
                    _currentUser.update { result.data }
                    onSuccess(result.data)
                } else if (result is UiState.Error) {
                    onError(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.toUserFriendlyMessage()
                _loginState.update { UiState.Error(msg) }
                onError(msg)
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        fullName: String = "",
        role: String = "PEMESAN",
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _registerState.update { UiState.Loading }
            try {
                val result = repository.register(email, pass, fullName, role)
                _registerState.update { result }
                if (result is UiState.Success && !result.data.requiresEmailConfirmation) {
                    _currentUser.update { result.data.user }
                } else if (result is UiState.Error) {
                    onError(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.toUserFriendlyMessage()
                _registerState.update { UiState.Error(msg) }
                onError(msg)
            }
        }
    }

    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Always clear local session even if signOut call fails
            } finally {
                _currentUser.update { null }
                _loginState.update { UiState.Idle }
                _registerState.update { UiState.Idle }
                _actionState.update { UiState.Idle }
                onSuccess()
            }
        }
    }
}
