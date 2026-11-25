package com.managevent.app.core.util

fun Throwable?.toUserFriendlyMessage(): String {
    if (this == null) return "Terjadi kesalahan. Silakan coba lagi."
    val raw = this.localizedMessage ?: this.message ?: ""
    return parseRawErrorMessage(raw)
}

fun parseRawErrorMessage(rawMessage: String?): String {
    if (rawMessage.isNullOrBlank()) return "Terjadi kesalahan. Silakan coba lagi."

    val lower = rawMessage.lowercase()
    return when {
        lower.contains("missing email") || lower.contains("missing phone") ->
            "Harap isi email dan password Anda."
        lower.contains("invalid_credentials") || lower.contains("invalid login credentials") || lower.contains("grant_type=password") ->
            "Email atau password yang Anda masukkan salah."
        lower.contains("email_not_confirmed") || lower.contains("email not confirmed") ->
            "Email Anda belum dikonfirmasi. Silakan cek kotak masuk email Anda."
        lower.contains("user_already_exists") || lower.contains("already registered") ->
            "Email ini sudah terdaftar. Silakan gunakan email lain atau masuk."
        lower.contains("invalid_email") || lower.contains("unable to validate email") ->
            "Format email tidak valid."
        lower.contains("password should be at least") || lower.contains("weak_password") ->
            "Password minimal 6 karakter."
        lower.contains("same_password") || lower.contains("different from the old password") ->
            "Password baru tidak boleh sama dengan password lama."
        lower.contains("rate_limit") || lower.contains("over_email_send_rate_limit") || lower.contains("too many requests") ->
            "Terlalu banyak permintaan. Silakan tunggu beberapa saat."
        lower.contains("row-level security") || lower.contains("permission denied") || lower.contains("42501") ->
            "Anda tidak memiliki izin untuk melakukan tindakan ini."
        lower.contains("duplicate key") || lower.contains("23505") ->
            "Data ini sudah ada sebelumnya."
        lower.contains("22p02") || lower.contains("invalid input syntax for type uuid") ->
            "Format ID data tidak valid."
        lower.contains("pgrst204") || lower.contains("could not find the") ->
            "Skema database belum sinkron. Jalankan docs/supabase_schema.sql di Supabase."
        lower.contains("network") || lower.contains("unknownhost") || lower.contains("connectexception") || lower.contains("timeout") ->
            "Koneksi internet terganggu. Periksa jaringan Anda."
        lower.contains("url:") || lower.contains("headers:") || lower.contains("http method:") -> {
            if (lower.contains("validation_failed")) {
                "Harap lengkapi semua data dengan benar."
            } else {
                "Gagal memproses data. Silakan coba lagi nanti."
            }
        }
        lower.contains("validation_failed") ->
            "Harap lengkapi semua kolom wajib dengan benar."
        else -> "Gagal memproses permintaan. Silakan coba lagi."
    }
}
