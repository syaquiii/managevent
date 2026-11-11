package com.managevent.app.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: String = "",
    val email: String = "",
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val role: String = "PEMESAN" // "VENDOR" atau "PEMESAN"
)
