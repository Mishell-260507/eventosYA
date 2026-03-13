package com.mishell.eventosya.ui.screens.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val userRole: String? = null
)

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val auth = FirebaseAuth.getInstance()

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = null, errorMessage = null)
    }

    private fun validateFields(): Boolean {
        val email = uiState.email.trim()
        val password = uiState.password
        var isValid = true

        if (email.isBlank()) {
            uiState = uiState.copy(emailError = "El correo no puede estar vacío")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = uiState.copy(emailError = "Formato de correo inválido")
            isValid = false
        }

        if (password.isBlank()) {
            uiState = uiState.copy(passwordError = "La contraseña no puede estar vacía")
            isValid = false
        }

        return isValid
    }

    fun loginWithEmail() {
        if (!validateFields()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                auth.signInWithEmailAndPassword(uiState.email.trim(), uiState.password).await()
                uiState = uiState.copy(isLoading = false, isLoginSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun sendPasswordReset() {
        val email = uiState.email.trim()
        if (email.isBlank()) {
            uiState = uiState.copy(emailError = "Ingresa tu correo para recuperar la contraseña")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
            try {
                auth.sendPasswordResetEmail(email).await()
                uiState = uiState.copy(
                    isLoading = false, 
                    infoMessage = "Se ha enviado un correo para restablecer tu contraseña"
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al enviar correo de recuperación"
                )
            }
        }
    }

    fun handleGoogleSignInResult(credential: Credential) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                firebaseAuthWithGoogle(idToken)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error al procesar el token de Google: ${e.localizedMessage}")
            }
        } else {
            uiState = uiState.copy(errorMessage = "Credencial de Google no válida o cancelada")
        }
    }

    fun onGoogleSignInError(error: String) {
        uiState = uiState.copy(errorMessage = "Error de Google: $error")
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                uiState = uiState.copy(isLoading = false, isLoginSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Error Firebase: ${e.localizedMessage}"
                )
            }
        }
    }
}
