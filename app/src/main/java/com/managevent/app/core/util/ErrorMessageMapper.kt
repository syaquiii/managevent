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
        lower.contains("user_already_exists") || lower.contains("already registered") ->
            "Email ini sudah terdaftar. Silakan gunakan email lain atau masuk."
        lower.contains("invalid_email") || lower.contains("unable to validate email") ->
            "Format email tidak valid."
        lower.contains("password should be at least") || lower.contains("weak_password") ->
            "Password minimal 6 karakter."
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
