package com.managevent.app.features.wishlist.data

import kotlinx.serialization.Serializable

@Serializable
data class WishlistModel(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val createdAt: String = ""
)
