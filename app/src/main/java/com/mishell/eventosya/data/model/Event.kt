package com.mishell.eventosya.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var date: Timestamp? = null,
    var location: String = "",
    var imageUrl: String? = null,
    var capacity: Long = 0, // Cambiado a Long para compatibilidad total con Firestore
    var registeredUsers: List<String> = emptyList(),
    var category: String = "General"
)
