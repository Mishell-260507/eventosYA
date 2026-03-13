package com.mishell.eventosya.data.model

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp? = null,
    val location: String = "",
    val imageUrl: String? = null,
    val capacity: Int = 0,
    val registeredUsers: List<String> = emptyList(),
    val category: String = "General"
)
