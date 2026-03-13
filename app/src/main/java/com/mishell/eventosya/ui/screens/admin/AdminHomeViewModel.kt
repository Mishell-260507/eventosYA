package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mishell.eventosya.data.model.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminHomeUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminHomeViewModel : ViewModel() {
    var uiState by mutableStateOf(AdminHomeUiState())
        private set

    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val snapshot = firestore.collection("events").get().await()
                val events = snapshot.toObjects(Event::class.java)
                uiState = uiState.copy(events = events, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al cargar eventos"
                )
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("events").document(eventId).delete().await()
                loadEvents() // Recargar lista
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error al eliminar: ${e.localizedMessage}")
            }
        }
    }
}
