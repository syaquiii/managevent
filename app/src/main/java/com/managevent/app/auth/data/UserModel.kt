package com.managevent.app.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: String,
    val email: String,
    val fullName: String? = null,
    val avatarUrl: String? = null
)
