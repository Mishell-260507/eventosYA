package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val auth = FirebaseAuth.getInstance()

    fun onTitleChange(value: String) { uiState = uiState.copy(title = value) }
    fun onDescriptionChange(value: String) { uiState = uiState.copy(description = value) }
    fun onLocationChange(value: String) { uiState = uiState.copy(location = value) }
    fun onCapacityChange(value: String) { uiState = uiState.copy(capacity = value) }
    fun onCategoryChange(value: String) { uiState = uiState.copy(category = value) }

    fun saveEvent() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            uiState = uiState.copy(errorMessage = "Error: Usuario no autenticado")
            return
        }

        val title = uiState.title.trim()
        val description = uiState.description.trim()
        val location = uiState.location.trim()
        val capacity = uiState.capacity.toLongOrNull() ?: 0L

        if (title.isBlank() || location.isBlank()) {
            uiState = uiState.copy(errorMessage = "Título y Ubicación son obligatorios")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val eventId = UUID.randomUUID().toString()
                
                val eventData = hashMapOf(
                    "id" to eventId,
                    "title" to title,
                    "description" to description,
                    "location" to location,
                    "capacity" to capacity,
                    "category" to uiState.category,
                    "date" to Timestamp(Date()),
                    "createdBy" to currentUser.uid,
                    "registeredUsers" to emptyList<String>()
                )

                firestore.collection("events")
                    .document(eventId)
                    .set(eventData)
                    .await()
                
                uiState = uiState.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar: ${e.localizedMessage}"
                )
            }
        }
    }
}
