package com.managevent.app.auth.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AuthRepository {
    private val client = SupabaseClientProvider.client

    suspend fun login(emailInput: String, passwordInput: String): UiState<UserModel> {
        return withContext(Dispatchers.IO) {
            try {
                if (emailInput.isBlank() || passwordInput.isBlank()) {
                    return@withContext UiState.Error("Harap isi email dan password Anda.")
                }
                client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }
                val currentAuthUser = client.auth.currentUserOrNull()
                if (currentAuthUser != null) {
                    val profile = fetchProfile(currentAuthUser.id, currentAuthUser.email ?: emailInput)
                    UiState.Success(profile)
                } else {
                    UiState.Error("Gagal mendapatkan data pengguna.")
                }
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun register(
        emailInput: String,
        passwordInput: String,
        fullName: String = "",
        role: String = "PEMESAN"
    ): UiState<UserModel> {
        return withContext(Dispatchers.IO) {
            try {
                if (emailInput.isBlank() || passwordInput.isBlank()) {
                    return@withContext UiState.Error("Harap lengkapi email dan password Anda.")
                }
                client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                    data = buildJsonObject {
                        put("full_name", fullName)
                        put("role", role)
                    }
                }
                val currentAuthUser = client.auth.currentUserOrNull()
                val userModel = if (currentAuthUser != null) {
                    UserModel(
                        id = currentAuthUser.id,
                        email = emailInput,
                        fullName = fullName,
                        role = role
                    )
                } else {
                    UserModel(
                        id = "user-${System.currentTimeMillis()}",
                        email = emailInput,
                        fullName = fullName,
                        role = role
                    )
                }
                UiState.Success(userModel)
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    suspend fun fetchCurrentProfile(): UserModel? {
        return withContext(Dispatchers.IO) {
            try {
                val currentAuthUser = client.auth.currentUserOrNull() ?: return@withContext null
                fetchProfile(currentAuthUser.id, currentAuthUser.email.orEmpty())
            } catch (_: Exception) {
                null
            }
        }
    }

    private suspend fun fetchProfile(userId: String, email: String): UserModel {
        return try {
            val response = client.postgrest["profiles"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserModel>()

            if (response != null) {
                response
            } else {
                val rawMeta = client.auth.currentUserOrNull()?.userMetadata as? JsonObject
                val fullName = rawMeta?.get("full_name")?.jsonPrimitive?.content ?: ""
                val role = rawMeta?.get("role")?.jsonPrimitive?.content ?: "PEMESAN"
                UserModel(id = userId, email = email, fullName = fullName, role = role)
            }
        } catch (_: Exception) {
            UserModel(id = userId, email = email, fullName = "Pengguna Managevent", role = "PEMESAN")
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                client.auth.signOut()
            } catch (_: Exception) {
                // Ignore logout exceptions
            }
        }
    }
}
