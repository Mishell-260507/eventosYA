# eventosYA 

**eventosYA** es una aplicación Android moderna diseñada para la gestión y visualización de eventos. Construida con las últimas tecnologías de desarrollo nativo, ofrece una experiencia fluida tanto para administradores como para usuarios finales.

##  Características

### Gestión de Usuarios
*   **Autenticación Segura:** Inicio de sesión y registro integrados con **Firebase Auth**.
*   **Google Sign-In:** Soporte para **Credential Manager** y Google ID para un acceso rápido.
*   **Perfiles de Usuario:** Gestión de información personal y preferencias desde el panel de perfil.

### Panel de Administración
*   **Visualización de Eventos:** Lista detallada de eventos disponibles.
*   **Creación de Eventos:** Formulario interactivo para dar de alta nuevos eventos en tiempo real.
*   **Control de Tickets:** Gestión y visualización de entradas/tickets emitidos.

### Experiencia de Usuario (UX)
*   **Material Design 3:** Interfaz moderna siguiendo las últimas guías de diseño de Google.
*   **Temas Dinámicos:** Soporte para colores dinámicos (Material You) que se adaptan al fondo de pantalla del usuario.
*   **Navegación Adaptativa:** Menú lateral (Navigation Rail) para tablets/pantallas grandes y barra inferior para móviles.

##  Stack Tecnológico

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarativa)
*   **Arquitectura:** MVVM (Model-View-ViewModel)
*   **Base de Datos y Auth:** [Firebase](https://firebase.google.com/) (Firestore & Authentication)
*   **Persistencia Local:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (para configuraciones de tema)
*   **Inyección de Dependencias:** ViewModel Compose

##  Estructura del Proyecto

```text
com.mishell.eventosya/
├── data/               # Modelos de datos y preferencias (DataStore)
├── ui/                 # Componentes de interfaz de usuario
│   ├── screens/        # Pantallas (Login, Register, Admin)
│   └── theme/          # Configuración de Material Theme y Colores
└── MainActivity.kt     # Punto de entrada y lógica de navegación principal
```

##  Configuración Requerida

1.  **Firebase:** Es necesario configurar un proyecto en la consola de Firebase y añadir el archivo `google-services.json` en la carpeta `app/`.
2.  **SDK Mínimo:** Android 8.0 (API 26) o superior.
3.  **Android Studio:** Se recomienda la última versión estable (Koala o superior) para soporte total de Compose.

---
Desarrollado para facilitar la creación y coordinación de eventos.
