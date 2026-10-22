package com.managevent.app.auth.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val client = SupabaseClientProvider.client

    suspend fun login(emailInput: String, passwordInput: String): UiState<UserModel> {
        return withContext(Dispatchers.IO) {
            try {
                client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                val currentUser = client.auth.currentUserOrNull()
                if (currentUser != null) {
                    UiState.Success(
                        UserModel(
                            id = currentUser.id,
                            email = currentUser.email ?: emailInput
                        )
                    )
                } else {
                    UiState.Error("Gagal mendapatkan data pengguna")
                }
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Login gagal")
            }
        }
    }

    suspend fun register(emailInput: String, passwordInput: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                UiState.Success("Registrasi berhasil. Silakan cek email untuk verifikasi.")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Registrasi gagal")
            }
        }
    }
}
