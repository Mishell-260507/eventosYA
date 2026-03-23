package com.mishell.eventosya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.mishell.eventosya.data.preferences.ThemePreferences
import com.mishell.eventosya.ui.screens.admin.AdminNavigation
import com.mishell.eventosya.ui.screens.login.LoginScreen
import com.mishell.eventosya.ui.screens.login.LoginViewModel
import com.mishell.eventosya.ui.screens.register.RegisterScreen
import com.mishell.eventosya.ui.screens.register.RegisterViewModel
import com.mishell.eventosya.ui.theme.EventosYATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences = remember { ThemePreferences(applicationContext) }
            val dynamicColorEnabled by preferences.dynamicColorEnabled.collectAsState(initial = false)
            
            // ESTADO MAESTRO: Detecta si hay un usuario logueado en tiempo real
            var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
            var isRegistering by remember { mutableStateOf(false) }

            // Escuchador de sesión único y global
            LaunchedEffect(Unit) {
                FirebaseAuth.getInstance().addAuthStateListener { auth ->
                    user = auth.currentUser
                }
            }

            EventosYATheme(dynamicColor = dynamicColorEnabled) {
                Crossfade(targetState = user != null, label = "MainNav") { isLoggedIn ->
                    if (isLoggedIn) {
                        // SI HAY USUARIO: Solo mostramos Admin
                        AdminNavigation(
                            onLogout = { 
                                FirebaseAuth.getInstance().signOut() 
                            }
                        )
                    } else {
                        // SI NO HAY USUARIO: Mostramos Login o Registro
                        if (isRegistering) {
                            val registerViewModel: RegisterViewModel = viewModel()
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = { isRegistering = false },
                                onBack = { isRegistering = false }
                            )
                        } else {
                            val loginViewModel: LoginViewModel = viewModel()
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { /* El listener se encargará */ },
                                onNavigateToRegister = { isRegistering = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
