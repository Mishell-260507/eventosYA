package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.mishell.eventosya.data.model.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

data class CreateEventUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val capacity: String = "",
    val category: String = "General",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateEventViewModel : ViewModel() {
    var uiState by mutableStateOf(CreateEventUiState())
        private set

    private val firestore = FirebaseFirestore.getInstance()

    fun onTitleChange(value: String) { uiState = uiState.copy(title = value) }
    fun onDescriptionChange(value: String) { uiState = uiState.copy(description = value) }
    fun onLocationChange(value: String) { uiState = uiState.copy(location = value) }
    fun onCapacityChange(value: String) { uiState = uiState.copy(capacity = value) }
    fun onCategoryChange(value: String) { uiState = uiState.copy(category = value) }

    fun saveEvent() {
        val title = uiState.title.trim()
        val description = uiState.description.trim()
        val location = uiState.location.trim()
        val capacity = uiState.capacity.toIntOrNull() ?: 0

        if (title.isBlank() || location.isBlank()) {
            uiState = uiState.copy(errorMessage = "Título y Ubicación son obligatorios")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val eventId = UUID.randomUUID().toString()
                val event = Event(
                    id = eventId,
                    title = title,
                    description = description,
                    location = location,
                    capacity = capacity,
                    category = uiState.category,
                    date = Timestamp(Date()) // Fecha actual por defecto
                )

                firestore.collection("events").document(eventId).set(event).await()
                uiState = uiState.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al guardar el evento"
                )
            }
        }
    }
}
