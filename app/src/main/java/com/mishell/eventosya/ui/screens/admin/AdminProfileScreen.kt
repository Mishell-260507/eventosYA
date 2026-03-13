package com.mishell.eventosya.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    viewModel: AdminProfileViewModel,
    onLogout: () -> Unit = {}
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil Admin") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sección de Usuario
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.user?.displayName ?: "Administrador",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = state.user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Rol: ${state.user?.role?.uppercase() ?: "ADMIN"}") }
                    )
                }
            }

            // Sección de Configuración de Tema
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text("Color Dinámico") },
                        supportingContent = { Text("Usa colores de tu fondo de pantalla (Android 12+)") },
                        leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                        trailingContent = {
                            Switch(
                                checked = state.isDynamicColorEnabled,
                                onCheckedChange = viewModel::toggleDynamicColor
                            )
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Alto Contraste") },
                        supportingContent = { Text("Aumenta la legibilidad de los textos") },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                        trailingContent = {
                            Switch(
                                checked = state.isHighContrastEnabled,
                                onCheckedChange = viewModel::toggleHighContrast
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Cerrar Sesión
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
