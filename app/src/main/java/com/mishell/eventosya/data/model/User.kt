package com.mishell.eventosya.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "user", // "admin" o "user"
    val photoUrl: String? = null
)
