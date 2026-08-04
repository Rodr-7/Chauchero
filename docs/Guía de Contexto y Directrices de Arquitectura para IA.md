# Guía de Contexto y Directrices de Arquitectura para IA (LLM)

> **Nota de Implementación para el Usuario:** Al iniciar una nueva sesión de desarrollo con un LLM, adjunta tu documentación del proyecto junto con este archivo y usa el siguiente prompt: *"Lee esta guía de contexto antes de generar o modificar cualquier línea de código del proyecto Chauchero"*.
> 

### 1. Propósito del Documento y Rol de la Inteligencia Artificial

Este documento define las reglas de arquitectura, convenciones de código, invariantes de negocio y el mapeo de la documentación oficial del proyecto **Chauchero** (App de Presupuesto Personal para Android).

- **Rol asignado al LLM:** Actuarás como un Ingeniero de Software Android Senior especializado en **Kotlin**, **Jetpack Compose**, **Room Database** y arquitectura **MVVM**.
- **Objetivo principal:** Garantizar que toda generación de código, refactorización o propuesta técnica sea 100% coherente con los requisitos funcionales (RF), casos de uso (CU), diagramas UML y escenarios límite definidos en la documentación del proyecto.
- **Principio de no contradicción:** Nunca generarás lógica ni estructuras de datos que violen las reglas financieras o de arquitectura aquí descritas sin previa confirmación explícita del desarrollador.

### 2. Control de Alcance (MVP vs. Backlog)

- **Límites del MVP (Minimum Viable Product):** El sistema actual es 100% *offline* y local.
- **Restricciones de Integración:** No generes dependencias de Firebase, Retrofit, ni APIs externas.
- **Autenticación:** No implementes sistemas de inicio de sesión (Login/Auth); la app se basa exclusivamente en un perfil único local almacenado en Room.

### 3. Pila Tecnológica Estricta (Tech Stack)

| **Capa / Componente** | **Tecnología Obligatoria** | **Versión / Patrón Requerido** |
| --- | --- | --- |
| **Lenguaje Principal** | Kotlin | Sintaxis moderna, tipado estricto y manejo de nulidad (`Null Safety`) |
| **UI Toolkit** | Jetpack Compose | Interfaz 100% declarativa (estrictamente prohibido el uso de XML Views) |
| **Arquitectura de UI** | MVVM + UDF | Separación clara en `ui`, `domain/model` y `data` (Unidirectional Data Flow) |
| **Base de Datos Local** | Room Database (SQLite) | Entidades anotadas, DAOs reactivos y repositorios |
| **Asincronía y Reactividad** | Kotlin Coroutines & Flow | Operaciones de I/O fuera del hilo principal (`Dispatchers.IO`) |

### 4. Contexto de Versiones Android y Dependencias

- **Diseño:** Utiliza obligatoriamente la sintaxis y componentes de **Material Design 3** (`androidx.compose.material3`).
- **Inyección de Dependencias:** Asume el uso de instanciación manual o `ViewModelProvider.Factory` a menos que el usuario especifique explícitamente el uso de Hilt/Dagger.
- **Navegación:** La navegación entre pantallas debe basarse en **Jetpack Navigation Compose**. Evita librerías obsoletas (ej. *Accompanist* para navegación).

### 5. Estructura de Paquetes Requerida (Project Structure)

Todo el código generado debe respetar la siguiente jerarquía bajo el paquete raíz del proyecto, por ejemplo `com.tuusuario.chauchero`:

* `/data`

  * `/local`

    * `/dao`: interfaces DAO de Room.
    * `/entity`: entidades persistentes como `GastoEntity` y `PerfilUsuarioEntity`.
    * `/database`: configuración de `AppDatabase`, converters y migraciones.
  * `/repository`: implementaciones de los repositorios y coordinación del acceso a datos.

* `/domain`

  * `/model`: modelos puros utilizados por la lógica de negocio y la interfaz.
  * `/usecase`: casos de uso cuando la lógica de negocio justifique separar operaciones del ViewModel.

* `/ui`

  * `/components`: componentes Composable reutilizables.
  * `/navigation`: rutas y configuración de Navigation Compose.
  * `/theme`: configuración de colores, tipografías y formas de Material Design 3.
  * `/screens`: funciones Composable correspondientes a cada pantalla.
  * `/viewmodels`: ViewModels que exponen estado inmutable y procesan eventos de la interfaz.

#### Reglas de separación

* Las entidades de Room deben permanecer en `data/local/entity`.
* Los modelos de dominio deben permanecer en `domain/model`.
* No se debe utilizar un paquete `/model` directamente en la raíz.
* Los DAOs no deben ser utilizados directamente desde las pantallas ni desde los Composables.
* Los repositorios coordinan la comunicación entre los DAOs y los ViewModels o casos de uso.
* Los ViewModels exponen el estado mediante `StateFlow` de solo lectura.
* Los Composables solamente representan estado y emiten eventos.
* No se deben duplicar modelos sin necesidad. Si una entidad Room y el modelo de dominio tienen exactamente la misma estructura y no existe una transformación justificable, se puede usar temporalmente la entidad como modelo interno, manteniendo claramente documentada esta decisión.


### 6. Mapeo y Lectura de la Documentación del Proyecto

Al procesar los archivos del proyecto, conecta la información técnica según la siguiente jerarquía:

- **Documento de Requisitos de Software (DRS.md):** Contiene la fuente de verdad de los Requisitos Funcionales (**RF-01 al RF-10**).
- **Modelo Entidad-Relación y Diagramas:** Define la estructura para **`PerfilUsuario`** y **`Gasto`**, flujos de navegación, máquinas de estado y secuencia de llamadas (DAOs/ViewModels).
- **Wireframes:** Definen la disposición visual de los componentes en Jetpack Compose, jerarquía de textos y estados de alerta.
- **Matriz de Casos Límite (TC-01 a TC-08):** Establece el comportamiento obligatorio ante escenarios de borde, desbordamientos y datos nulos.

### 7. Reglas de Negocio e Invariantes Financieros

Todo cálculo aritmético en DAOs, Repositorios o ViewModels debe respetar estas reglas:

| **Cálculo Financiero** | **Referencia** | **Fórmula / Lógica Requerida** | **Regla Defensiva Ante Nulos** |
| --- | --- | --- | --- |
| **Total Gastos Fijos** | RF-05 | Suma de todos los gastos  | Usar `COALESCE(SUM(valor), 0)` en SQL |
| **Total Libre Mensual** | RF-06 | `salario_fijo` - `Total Gastos Fijos` | Si `salario_fijo` es `0` o `null` (TC-01), procesar sin romper la UI |
| **Total Por Pagar** | RF-08 | Suma de gastos donde `estado_pagado = false` | Excluir transacciones ya pagadas (TC-05) |
| **Libre Mens. Aproximado** | RF-09 | `saldo_actual` - `Total Por Pagar` | Si el saldo es menor a la deuda, mostrar en negativo/alerta (TC-03) |

### 8. Convenciones Obligatorias de Código (Kotlin & Compose)

- **Inmutabilidad de Estado:** Los ViewModels expondrán el estado hacia Compose únicamente mediante `StateFlow` de solo lectura (`asStateFlow()`).
- **Protección de Consultas (COALESCE):** Toda consulta de agregación (`SUM`, `COUNT`) debe estar envuelta en funciones `COALESCE(..., 0)` de SQLite para garantizar retornos enteros (`0`) en lugar de `NullPointerException`.
- **Manejo de Entrada de Texto:** Todo `TextField` debe validar su cadena con `.trim().isNotEmpty()` antes de habilitar los botones de guardado (TC-07).
- **Gestión de Hilos (Coroutines):** Ninguna operación de base de datos debe bloquear el hilo principal. Se usarán funciones `suspend` bajo `Dispatchers.IO` o flujos de Room (`Flow`).
- **Diseño Declarativo:** Las pantallas en Compose deben dividir su responsabilidades (Hoisting), separando componentes *Stateless* de los *Stateful*.

### 9. Instrucciones de Formato de Salida para la IA (Output Rules)

1. **Respuestas de Código Parcial:** Si modificas un archivo existente, **no reescribas todo el archivo**. Muestra únicamente la función, clase o bloque modificado, indicando su contexto claramente (ej. `// ... código anterior`).
2. **Cita de Fuentes:** Al explicar la lógica generada, referencia el identificador documental (ej: *"Se aplica la regla TC-04..."* o *"Cumpliendo con RF-05..."*).
3. **Manejo de Ambigüedades:** Si una solicitud del usuario contradice las fórmulas financieras o el límite del MVP, advierte el conflicto técnico antes de escribir el código.
4. **Comentarios de Calidad:** Entrega código con nombres autodescriptivos y agrega comentarios concisos (en español) solo en bloques de lógica matemática, inyección de corrutinas o consultas SQL complejas.
