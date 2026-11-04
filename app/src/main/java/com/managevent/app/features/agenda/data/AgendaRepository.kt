package com.managevent.app.features.agenda.data

import com.managevent.app.core.network.SupabaseClientProvider
import com.managevent.app.core.util.UiState
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgendaRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getAgendaByDate(userId: String, date: String): UiState<List<AgendaModel>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = client.postgrest["agenda"].select {
                    filter {
                        eq("user_id", userId)
                        eq("date", date)
                    }
                }.decodeList<AgendaModel>()
                UiState.Success(list)
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal mengambil daftar agenda")
            }
        }
    }

    suspend fun addAgenda(agenda: AgendaModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["agenda"].insert(agenda)
                UiState.Success("Catatan agenda berhasil ditambahkan")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menambahkan agenda")
            }
        }
    }

    suspend fun updateAgenda(agendaId: String, agenda: AgendaModel): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["agenda"].update(agenda) {
                    filter { eq("id", agendaId) }
                }
                UiState.Success("Catatan agenda berhasil diperbarui")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal memperbarui agenda")
            }
        }
    }

    suspend fun deleteAgenda(agendaId: String): UiState<String> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["agenda"].delete {
                    filter { eq("id", agendaId) }
                }
                UiState.Success("Catatan agenda berhasil dihapus")
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Gagal menghapus agenda")
            }
        }
    }
}
