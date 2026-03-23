package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    private var eventsListener: ListenerRegistration? = null

    init {
        observeEvents()
    }

    private fun observeEvents() {
        uiState = uiState.copy(isLoading = true)
        
        // Listener en tiempo real: detecta cambios automáticamente
        eventsListener = firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Error al observar eventos"
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val eventsList = snapshot.toObjects(Event::class.java)
                    uiState = uiState.copy(events = eventsList, isLoading = false)
                }
            }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("events").document(eventId).delete().await()
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error al eliminar: ${e.localizedMessage}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventsListener?.remove() // Limpiar listener para evitar fugas de memoria
    }
}
