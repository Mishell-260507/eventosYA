package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AdminTicketsUiState(
    val tickets: List<String> = emptyList(), // Placeholder para modelo de tickets
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminTicketsViewModel : ViewModel() {
    var uiState by mutableStateOf(AdminTicketsUiState())
        private set

    init {
        loadTickets()
    }

    fun loadTickets() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // Simulación de carga o consulta a Firestore
                delay(1000)
                uiState = uiState.copy(tickets = emptyList(), isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al cargar tickets"
                )
            }
        }
    }
}
