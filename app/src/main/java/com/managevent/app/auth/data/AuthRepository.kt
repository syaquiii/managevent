package com.managevent.app.auth.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import com.managevent.app.core.util.toUserFriendlyMessage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

data class RegisterResult(
    val user: UserModel,
    val requiresEmailConfirmation: Boolean
)

open class AuthRepository(
    private val clientProvider: () -> SupabaseClient = { SupabaseClientProvider.client }
) {
    private val client: SupabaseClient by lazy(clientProvider)

    open suspend fun login(emailInput: String, passwordInput: String): UiState<UserModel> {
        return withContext(Dispatchers.IO) {
            try {
                val trimmedEmail = emailInput.trim()
                if (trimmedEmail.isBlank() || passwordInput.isBlank()) {
                    return@withContext UiState.Error("Harap isi email dan password Anda.")
                }
                client.auth.signInWith(Email) {
                    email = trimmedEmail
                    password = passwordInput
                }
                val currentAuthUser = client.auth.currentUserOrNull()
                if (currentAuthUser != null) {
                    val profile = fetchProfile(currentAuthUser.id, currentAuthUser.email ?: trimmedEmail)
                    UiState.Success(profile)
                } else {
                    UiState.Error("Gagal mendapatkan data pengguna.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun register(
        emailInput: String,
        passwordInput: String,
        fullName: String = "",
        role: String = "PEMESAN"
    ): UiState<RegisterResult> {
        return withContext(Dispatchers.IO) {
            try {
                val trimmedEmail = emailInput.trim()
                val trimmedName = fullName.trim()
                if (trimmedEmail.isBlank() || passwordInput.isBlank()) {
                    return@withContext UiState.Error("Harap lengkapi email dan password Anda.")
                }
                if (passwordInput.length < 6) {
                    return@withContext UiState.Error("Password minimal 6 karakter.")
                }
                val normalizedRole = if (role.equals("VENDOR", ignoreCase = true)) "VENDOR" else "PEMESAN"

                client.auth.signUpWith(Email) {
                    email = trimmedEmail
                    password = passwordInput
                    data = buildJsonObject {
                        put("full_name", trimmedName)
                        put("role", normalizedRole)
                    }
                }

                val session = client.auth.currentSessionOrNull()
                val currentAuthUser = client.auth.currentUserOrNull()
                if (currentAuthUser != null && session != null) {
                    val userModel = UserModel(
                        id = currentAuthUser.id,
                        email = currentAuthUser.email ?: trimmedEmail,
                        fullName = trimmedName,
                        role = normalizedRole
                    )
                    // Ensure profile row matches selected role & name immediately
                    try {
                        client.postgrest["profiles"].upsert(userModel)
                    } catch (_: Exception) {
                        // Trigger handle_new_user handles creation on signUp
                    }
                    val profile = fetchProfile(currentAuthUser.id, userModel.email)
                    UiState.Success(RegisterResult(user = profile, requiresEmailConfirmation = false))
                } else {
                    UiState.Success(
                        RegisterResult(
                            user = UserModel(
                                id = currentAuthUser?.id.orEmpty(),
                                email = trimmedEmail,
                                fullName = trimmedName,
                                role = normalizedRole
                            ),
                            requiresEmailConfirmation = true
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun sendPasswordReset(emailInput: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                val trimmedEmail = emailInput.trim()
                if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                    return@withContext UiState.Error("Harap masukkan format email yang valid.")
                }
                client.auth.resetPasswordForEmail(trimmedEmail)
                UiState.Success("Tautan reset password telah dikirim ke $trimmedEmail.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun fetchCurrentProfile(): UserModel? {
        return withContext(Dispatchers.IO) {
            try {
                client.auth.awaitInitialization()
                val currentAuthUser = client.auth.currentUserOrNull() ?: return@withContext null
                fetchProfile(currentAuthUser.id, currentAuthUser.email.orEmpty())
            } catch (e: CancellationException) {
                throw e
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

    open suspend fun updateProfile(userId: String, fullName: String, role: String): UiState<UserModel> {
        return withContext(Dispatchers.IO) {
            try {
                val activeUid = userId.ifBlank { client.auth.currentUserOrNull()?.id.orEmpty() }
                if (activeUid.isBlank()) {
                    return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                }
                val trimmedName = fullName.trim()
                if (trimmedName.isBlank()) {
                    return@withContext UiState.Error("Nama lengkap tidak boleh kosong.")
                }
                val normalizedRole = if (role.equals("VENDOR", ignoreCase = true)) "VENDOR" else "PEMESAN"
                val email = client.auth.currentUserOrNull()?.email.orEmpty()
                val updatedUser = UserModel(
                    id = activeUid,
                    email = email,
                    fullName = trimmedName,
                    role = normalizedRole
                )
                client.postgrest["profiles"].upsert(updatedUser)
                try {
                    client.auth.updateUser {
                        data = buildJsonObject {
                            put("full_name", trimmedName)
                            put("role", normalizedRole)
                        }
                    }
                } catch (_: Exception) {
                    // Metadata sync is best-effort; profiles table is source of truth
                }
                UiState.Success(updatedUser)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun updatePassword(newPassword: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (client.auth.currentUserOrNull() == null) {
                    return@withContext UiState.Error("Silakan masuk terlebih dahulu.")
                }
                if (newPassword.length < 6) {
                    return@withContext UiState.Error("Password minimal 6 karakter.")
                }
                client.auth.updateUser {
                    password = newPassword
                }
                UiState.Success("Kata sandi berhasil diperbarui.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    open suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                client.auth.signOut()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Ignore logout exceptions
            }
        }
    }
}
