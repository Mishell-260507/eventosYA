package com.mishell.eventosya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mishell.eventosya.data.preferences.ThemePreferences
import com.mishell.eventosya.ui.screens.admin.AdminNavigation
import com.mishell.eventosya.ui.screens.login.LoginScreen
import com.mishell.eventosya.ui.screens.login.LoginViewModel
import com.mishell.eventosya.ui.screens.register.RegisterScreen
import com.mishell.eventosya.ui.screens.register.RegisterViewModel
import com.mishell.eventosya.ui.theme.EventosYATheme

sealed class Screen {
    data object Login : Screen()
    data object Register : Screen()
    data object Admin : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences = remember { ThemePreferences(applicationContext) }
            val dynamicColorEnabled by preferences.dynamicColorEnabled.collectAsState(initial = false)
            
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

            EventosYATheme(
                dynamicColor = dynamicColorEnabled
            ) {
                when (currentScreen) {
                    Screen.Login -> {
                        val loginViewModel: LoginViewModel = viewModel()
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { currentScreen = Screen.Admin },
                            onNavigateToRegister = { currentScreen = Screen.Register }
                        )
                    }
                    Screen.Register -> {
                        val registerViewModel: RegisterViewModel = viewModel()
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onRegisterSuccess = { currentScreen = Screen.Login },
                            onBack = { currentScreen = Screen.Login }
                        )
                    }
                    Screen.Admin -> {
                        AdminNavigation()
                    }
                }
            }
        }
    }
}
