# Capas MVVM (Model-View-ViewModel)

```
com.rodr.chauchero
│
├── data/                   # CAPA DE DATOS (Single Source of Truth)
│   ├── local/              # Base de datos Room
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt   # TypeConverter para el Enum Prioridad
│   │   ├── GastoDao.kt
│   │   └── PerfilUsuarioDao.kt
│   └── repository/         # Repositorios (Mediadores)
│       ├── GastoRepository.kt
│       └── PerfilUsuarioRepository.kt  <-- [AGREGADO]
│
├── model/                  # MODELOS DE DOMINIO / ENTIDADES
│   ├── Gasto.kt            # Entidad Gasto
│   ├── PerfilUsuario.kt    # Entidad PerfilUsuario
│   └── Prioridad.kt        # Enum (ALTO, MEDIO, BAJO)
│
└── ui/                     # CAPA DE PRESENTACIÓN
├── navigation/         # Navegación y rutas
│   └── AppNavigation.kt
├── theme/              # <-- [AGREGADO] Configuración de Jetpack Compose
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── components/         # <-- [AGREGADO] Componentes UI reutilizables
│   ├── GastoCard.kt    # Tarjetas de gasto con tachado condicional
│   └── CategoriaChip.kt# Chips de categorías redondeados
├── screens/            # Vistas por módulo funcional
│   ├── onboarding/
│   │   └── OnboardingScreen.kt
│   ├── presupuesto/
│   │   └── PresupuestoScreen.kt
│   ├── gastos/
│   │   ├── ListaGastosScreen.kt
│   │   └── NuevoGastoScreen.kt     <-- [AGREGADO]
│   └── ajustes/                    <-- [AGREGADO]
│       └── AjustesScreen.kt
└── viewmodels/         # Controladores de estado (StateFlow)
├── PresupuestoViewModel.kt
├── GastosViewModel.kt
└── OnboardingViewModel.kt      <-- [AGREGADO]
```

## Resumen de Responsabilidades por Capa

| **Capa** | **Paquete** | **Responsabilidad Principal** |
| --- | --- | --- |
| **Datos** | `data/local` | Almacenamiento local persistente en SQLite vía Room. |
| **Datos** | `data/repository` | Abstracción y acceso único a los datos para los ViewModels. |
| **Dominio** | `model` | Estructuras de datos puras y reglas conceptuales del negocio. |
| **Presentación** | `ui/theme` y `components` | Sistema de diseño, estilos y bloques visuales reutilizables. |
| **Presentación** | `ui/screens` y `navigation` | Pantallas de la aplicación y transiciones entre ellas. |
| **Presentación** | `ui/viewmodels` | Gestión del estado de la UI y lógica reactiva con el usuario. |

## 1. Capa de Datos (`data/`) — El Motor y la Memoria

Esta capa gestiona la persistencia y es la **única fuente de verdad** (*Single Source of Truth*) de la aplicación. Ninguna pantalla o ViewModel accede directamente a la base de datos sin pasar por aquí.

### `data/local/` (Base de Datos Room)

Contiene todo lo necesario para interactuar directamente con SQLite en el dispositivo:

- **`AppDatabase.kt`:** Es la clase principal de la base de datos Room. Define qué entidades existen y la versión de la base de datos.
- **`Converters.kt`:** Actúa como traductor. Convierte tipos complejos (como el enum `Prioridad`) en tipos primitivos (`Int` o `String`) que SQLite puede entender y almacenar.
- **`GastoDao.kt`:** *Data Access Object* de los gastos. Contiene las consultas SQL (crear, leer, actualizar, eliminar y marcar como pagado/pendiente).
- **`PerfilUsuarioDao.kt`:** *Data Access Object* del perfil. Gestiona las consultas sobre el salario mensual, saldo disponible y el estado del onboarding.

### `data/repository/` (Mediadores)

Sirve como puente entre la base de datos y la lógica de interfaz:

- **`GastoRepository.kt`:** Centraliza las operaciones sobre los gastos. Recibe peticiones del ViewModel, las ejecuta en el DAO y devuelve los datos limpios.
- **`PerfilUsuarioRepository.kt`:** Controla la lógica de acceso a los datos financieros del usuario y los cálculos de saldo restante.

## 2. Capa de Dominio (`model/`) — El Corazón del Negocio

Aquí viven los **modelos y entidades puras**. Son clases que representan los conceptos fundamentales del sistema y no dependen de la interfaz gráfica:

- **`Gasto.kt`:** Clase anotada con `@Entity` que define las propiedades de un gasto (ID, nombre, monto, categoría, prioridad, estado pagado/pendiente).
- **`PerfilUsuario.kt`:** Clase `@Entity` que almacena la información financiera base del usuario (ingreso mensual, saldo disponible y si ya completó el onboarding).
- **`Prioridad.kt`:** Enumeración (`Enum`) que restringe los niveles de importancia posible para un gasto (`ALTO`, `MEDIO`, `BAJO`).

## 3. Capa de Presentación (`ui/`) — Interfaz y Lógica Visual

Contiene todo el código que el usuario ve y con el que interactúa, dividida entre diseño visual (Jetpack Compose) y gestión de estado (ViewModels).

### `ui/navigation/`

- **`AppNavigation.kt`:** Define el grafo de navegación (`NavHost`). Administra las rutas para mover al usuario entre el Onboarding, Presupuesto, Lista de Gastos, Nuevo Gasto y Ajustes.

### `ui/theme/`

Configuración estética general en Jetpack Compose:

- **`Color.kt`:** Paleta de colores (tonos financieros, verdes para saldo positivo, rojos para alertas, etc.).
- **`Type.kt`:** Estilos tipográficos para títulos, montos y descripciones.
- **`Theme.kt`:** Orquesta colores y tipografía, adaptando la app a modo claro y oscuro.

### `ui/components/`

Piezas visuales modulares que se reutilizan en distintas pantallas para evitar código duplicado:

- **`GastoCard.kt`:** Tarjeta visual de un gasto individual, incluyendo la lógica visual para tacharse cuando se marca como pagado.
- **`CategoriaChip.kt`:** Etiqueta visual redondeada para mostrar la categoría o prioridad del gasto.

### `ui/screens/` (Vistas por Módulo Funcional)

Pantallas completas construidas en Compose:

- **`onboarding/OnboardingScreen.kt`:** Pantalla inicial de bienvenida y configuración del primer salario.
- **`presupuesto/PresupuestoScreen.kt`:** Dashboard principal con resumen financiero, progreso del salario disponible y accesos directos.
- **`gastos/ListaGastosScreen.kt`:** Vista con el historial general de gastos y filtros por categoría o prioridad.
- **`gastos/NuevoGastoScreen.kt`:** Formulario modal o pantalla para registrar y editar un gasto.
- **`ajustes/AjustesScreen.kt`:** Vista para modificar el salario base o reiniciar los datos del ciclo mes a mes.

### `ui/viewmodels/` (Controladores de Estado)

Conectan la UI con los repositorios usando flujos reactivos (`StateFlow`):

- **`PresupuestoViewModel.kt`:** Supervisa los cálculos financieros en tiempo real y expone el saldo disponible a la vista del dashboard.
- **`GastosViewModel.kt`:** Administra las listas de gastos, aplica filtros, crea nuevos gastos y procesa el cambio de estado entre pagado y pendiente.
- **`OnboardingViewModel.kt`:** Procesa y valida la entrada del salario inicial durante la primera ejecución de la app.