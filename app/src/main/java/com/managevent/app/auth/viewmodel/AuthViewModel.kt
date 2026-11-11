package com.managevent.app.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.managevent.app.auth.data.AuthRepository
import com.managevent.app.auth.data.UserModel
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
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

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    init {
        checkCurrentSession()
    }

    fun resetAuthState() {
        _loginState.value = UiState.Idle
    }

    fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val profile = repository.fetchCurrentProfile()
                if (profile != null) {
                    _currentUser.value = profile
                    _loginState.value = UiState.Success(profile)
                }
            } catch (_: Exception) {
                // Keep session idle
            }
        }
    }

    fun updateProfile(fullName: String, role: String) {
        val uid = _currentUser.value?.id.orEmpty()
        viewModelScope.launch {
            try {
                val result = repository.updateProfile(uid, fullName, role)
                if (result is UiState.Success) {
                    _currentUser.value = result.data
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _currentUser.value = _currentUser.value?.copy(fullName = fullName, role = role)
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            try {
                repository.updatePassword(newPassword)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Ignore
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
            _loginState.value = UiState.Loading
            try {
                val result = repository.login(email, pass)
                _loginState.value = result
                if (result is UiState.Success) {
                    _currentUser.value = result.data
                    onSuccess(result.data)
                } else if (result is UiState.Error) {
                    onError(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.toUserFriendlyMessage()
                _loginState.value = UiState.Error(msg)
                onError(msg)
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        fullName: String = "",
        role: String = "PEMESAN",
        onSuccess: (UserModel) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = repository.register(email, pass, fullName, role)
                _loginState.value = result
                if (result is UiState.Success) {
                    _currentUser.value = result.data
                    onSuccess(result.data)
                } else if (result is UiState.Error) {
                    onError(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = e.toUserFriendlyMessage()
                _loginState.value = UiState.Error(msg)
                onError(msg)
            }
        }
    }

    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.logout()
                _currentUser.value = null
                _loginState.value = UiState.Idle
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _currentUser.value = null
                _loginState.value = UiState.Idle
                onSuccess()
            }
        }
    }
}
