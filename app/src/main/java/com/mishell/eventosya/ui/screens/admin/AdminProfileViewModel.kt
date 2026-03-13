package com.mishell.eventosya.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mishell.eventosya.data.model.User
import com.mishell.eventosya.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminProfileUiState(
    val user: User? = null,
    val isDynamicColorEnabled: Boolean = false,
    val isHighContrastEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminProfileViewModel(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    var uiState by mutableStateOf(AdminProfileUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadUserProfile()
        loadThemePreferences()
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val doc = firestore.collection("users").document(currentUser.uid).get().await()
                val user = doc.toObject(User::class.java) ?: User(
                    uid = currentUser.uid,
                    email = currentUser.email ?: "",
                    displayName = currentUser.displayName ?: "Admin"
                )
                uiState = uiState.copy(user = user, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al cargar perfil"
                )
            }
        }
    }

    private fun loadThemePreferences() {
        viewModelScope.launch {
            val dynamicEnabled = themePreferences.dynamicColorEnabled.first()
            val contrastEnabled = themePreferences.highContrastEnabled.first()
            uiState = uiState.copy(
                isDynamicColorEnabled = dynamicEnabled,
                isHighContrastEnabled = contrastEnabled
            )
        }
    }

    fun toggleDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDynamicColorEnabled(enabled)
            uiState = uiState.copy(isDynamicColorEnabled = enabled)
        }
    }

    fun toggleHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setHighContrastEnabled(enabled)
            uiState = uiState.copy(isHighContrastEnabled = enabled)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
