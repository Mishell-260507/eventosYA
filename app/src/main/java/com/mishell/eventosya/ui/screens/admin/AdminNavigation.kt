package com.mishell.eventosya.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mishell.eventosya.data.preferences.ThemePreferences

sealed class AdminScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : AdminScreen("admin_home", "Eventos", Icons.Default.Event)
    data object Tickets : AdminScreen("admin_tickets", "Tickets", Icons.Default.ConfirmationNumber)
    data object Profile : AdminScreen("admin_profile", "Perfil", Icons.Default.Person)
    data object CreateEvent : AdminScreen("create_event", "Crear Evento", Icons.Default.Event)
}

@Composable
fun AdminNavigation() {
    var currentScreen by remember { mutableStateOf<AdminScreen>(AdminScreen.Home) }
    val items = listOf(AdminScreen.Home, AdminScreen.Tickets, AdminScreen.Profile)
    val context = LocalContext.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            bottomBar = {
                if (!useRail && currentScreen != AdminScreen.CreateEvent) {
                    NavigationBar {
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentScreen == screen,
                                onClick = { currentScreen = screen }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                if (useRail && currentScreen != AdminScreen.CreateEvent) {
                    NavigationRail(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items.forEach { screen ->
                            NavigationRailItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentScreen == screen,
                                onClick = { currentScreen = screen }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        AdminScreen.Home -> {
                            val homeViewModel: AdminHomeViewModel = viewModel()
                            AdminHomeScreen(
                                viewModel = homeViewModel,
                                onCreateEvent = { currentScreen = AdminScreen.CreateEvent }
                            )
                        }
                        AdminScreen.CreateEvent -> {
                            val createViewModel: CreateEventViewModel = viewModel()
                            CreateEventScreen(
                                viewModel = createViewModel,
                                onBack = { currentScreen = AdminScreen.Home }
                            )
                        }
                        AdminScreen.Tickets -> {
                            val ticketsViewModel: AdminTicketsViewModel = viewModel()
                            AdminTicketsScreen(viewModel = ticketsViewModel)
                        }
                        AdminScreen.Profile -> {
                            val themePrefs = remember { ThemePreferences(context.applicationContext) }
                            val profileViewModel: AdminProfileViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return AdminProfileViewModel(themePrefs) as T
                                    }
                                }
                            )
                            AdminProfileScreen(
                                viewModel = profileViewModel,
                                onLogout = { /* Navegar al login */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
