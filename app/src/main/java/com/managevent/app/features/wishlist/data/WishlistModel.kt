package com.managevent.app.features.wishlist.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishlistModel(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("event_id")
    val eventId: String = "",
    @SerialName("created_at")
    val createdAt: String? = null
)
