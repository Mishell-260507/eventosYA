package com.mishell.eventosya.ui.screens.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mishell.eventosya.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val displayNameError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = null, errorMessage = null)
    }

    fun onConfirmPasswordChange(password: String) {
        uiState = uiState.copy(confirmPassword = password, confirmPasswordError = null, errorMessage = null)
    }

    fun onDisplayNameChange(name: String) {
        uiState = uiState.copy(displayName = name, displayNameError = null, errorMessage = null)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val email = uiState.email.trim()
        val password = uiState.password
        val confirmPassword = uiState.confirmPassword
        val displayName = uiState.displayName.trim()

        if (displayName.isBlank()) {
            uiState = uiState.copy(displayNameError = "El nombre es obligatorio")
            isValid = false
        }

        if (email.isBlank()) {
            uiState = uiState.copy(emailError = "El correo es obligatorio")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = uiState.copy(emailError = "Formato de correo inválido")
            isValid = false
        }

        if (password.length < 6) {
            uiState = uiState.copy(passwordError = "La contraseña debe tener al menos 6 caracteres")
            isValid = false
        }

        if (confirmPassword != password) {
            uiState = uiState.copy(confirmPasswordError = "Las contraseñas no coinciden")
            isValid = false
        }

        return isValid
    }

    fun register() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val result = auth.createUserWithEmailAndPassword(uiState.email.trim(), uiState.password).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = uiState.email.trim(),
                        displayName = uiState.displayName.trim(),
                        role = "user" // Rol por defecto
                    )
                    
                    // Guardar en Firestore
                    firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
                    uiState = uiState.copy(isLoading = false, isRegisterSuccess = true)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al registrar usuario"
                )
            }
        }
    }
}
