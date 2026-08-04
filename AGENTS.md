# AGENTS.md — Proyecto Chauchero

## 1. Propósito

Este archivo contiene las instrucciones permanentes para cualquier agente de IA que analice, genere o modifique código del proyecto **Chauchero**.

Chauchero es una aplicación Android nativa de presupuesto personal orientada al registro de gastos fijos, seguimiento de pagos y cálculo de saldos. El MVP funciona de manera **local y offline**.

Actúa como un ingeniero Android senior especializado en:

- Kotlin.
- Jetpack Compose.
- Material Design 3.
- Arquitectura MVVM con flujo unidireccional de datos (UDF).
- Room Database.
- Kotlin Coroutines y Flow.
- Navigation Compose.

No inventes requisitos, pantallas, entidades, dependencias ni reglas financieras que no estén respaldadas por la documentación o por una instrucción explícita del desarrollador.

---

## 2. Fuentes obligatorias del proyecto

Antes de implementar una funcionalidad, consulta los documentos pertinentes ubicados en `docs/`:

1. `Documento de Requisitos de Software (DRS).md`
2. `Casos de Uso.md`
3. `Criterios de Aceptación y Escenarios Límite.md`
4. `Guía de Contexto y Directrices de Arquitectura para IA.md`
5. `Capas MVVM (Model-View-ViewModel).md`
6. `Diagramas y Modelos/Modelo Entidad-Relación.md`
7. `Diagramas y Modelos/Diagrama de Clases.md`
8. `Diagramas y Modelos/Diagramas de Secuencia.md`
9. `Wireframes.md` y los recursos visuales asociados.
10. `Product Backlog & Roadmap - Chauchero.md`

Si los documentos se encuentran dentro de otra subcarpeta, localízalos por nombre de forma recursiva. No dependas únicamente de los enlaces internos exportados desde Notion, ya que algunos pueden estar desactualizados.

### Orden de precedencia

Ante una contradicción, aplica el siguiente orden:

1. Instrucción explícita y actual del desarrollador.
2. Este `AGENTS.md`.
3. DRS y criterios de aceptación.
4. Modelo entidad-relación y diagrama de clases.
5. Casos de uso y diagramas de secuencia.
6. Documento de capas MVVM y guía de contexto.
7. Wireframes.
8. Product Backlog y Roadmap.

El Roadmap describe trabajo futuro y **no forma parte automáticamente del MVP**.

Antes de comenzar cada tarea, identifica qué requisitos `RF`, casos de uso `CU` y escenarios límite `TC` están involucrados.

---

## 3. Alcance del MVP

El MVP incluye:

- Aplicación nativa para Android.
- Un único perfil local almacenado en Room.
- Onboarding de primera ejecución.
- Registro de gastos fijos.
- Visualización de la lista de gastos.
- Cambio de estado entre pendiente y pagado.
- Edición y eliminación de gastos cuando la tarea lo requiera.
- Registro y actualización del salario fijo mensual.
- Registro y actualización del saldo bancario actual.
- Cálculo reactivo del resumen financiero.
- Persistencia completamente local.

No implementes sin autorización explícita:

- Firebase.
- Retrofit.
- APIs externas o servicios web.
- Inicio de sesión o autenticación.
- Sincronización en la nube.
- Conexión automática con bancos.
- Hilt o Dagger.
- Vistas XML.
- Gestión de múltiples perfiles.
- Gastos imprevistos o temporales.
- Reinicio de periodo.
- Notificaciones.
- Gráficos o análisis históricos.
- Exportación a PDF, CSV o Excel.
- Respaldo o importación de la base de datos.
- Funcionalidades del Roadmap no solicitadas expresamente.

No agregues permisos de red ni dependencias de conectividad para el MVP.

---

## 4. Pila tecnológica obligatoria

- **Lenguaje:** Kotlin con null safety.
- **Interfaz:** Jetpack Compose exclusivamente.
- **Diseño:** Material Design 3 mediante `androidx.compose.material3`.
- **Arquitectura:** MVVM + UDF.
- **Persistencia:** Room sobre SQLite.
- **Reactividad:** Kotlin Coroutines, Flow y StateFlow.
- **Navegación:** Jetpack Navigation Compose.
- **Inyección de dependencias:** instanciación manual o `ViewModelProvider.Factory`, salvo instrucción explícita distinta.

Respeta las versiones existentes en Gradle. No actualices Kotlin, Gradle, Android Gradle Plugin, Compose, Room ni otras dependencias salvo que sea imprescindible para la tarea y se explique el motivo.

---

## 5. Estructura real del repositorio

El paquete raíz oficial es:

```text
com.rodr.chauchero
```

La estructura base que debe respetarse es:

```text
com.rodr.chauchero/
├── data/
│   ├── local/
│   └── repository/
├── model/
├── ui/
│   ├── navigation/
│   ├── theme/
│   ├── components/
│   ├── screens/
│   └── viewmodels/
└── MainActivity.kt
```

La estructura del repositorio existente tiene prioridad sobre cualquier estructura arquitectónica genérica.

### `data/local`

Contiene los elementos directamente relacionados con Room:

- `AppDatabase.kt`.
- `Converters.kt`.
- `GastoDao.kt`.
- `PerfilUsuarioDao.kt`.
- Migraciones, si existen.

### `data/repository`

Contiene los repositorios que median entre los DAOs y los ViewModels:

- `GastoRepository.kt`.
- `PerfilUsuarioRepository.kt`.

### `model`

Contiene los modelos y entidades usados por la aplicación:

- `Gasto.kt`.
- `PerfilUsuario.kt`.
- `Prioridad.kt`.

Según la documentación actual, `Gasto` y `PerfilUsuario` pueden ser entidades Room ubicadas en `model`. Mantén esta decisión mientras el repositorio la use.

### `ui`

Contiene la presentación:

- `navigation`: grafo y rutas de Navigation Compose.
- `theme`: colores, tipografías, formas y tema Material 3.
- `components`: Composables reutilizables.
- `screens`: pantallas organizadas por módulo.
- `viewmodels`: ViewModels y estados de la interfaz.

### `MainActivity.kt`

Debe permanecer directamente dentro de `com.rodr.chauchero` y actuar principalmente como punto de entrada de Compose y de la navegación.

No coloques consultas Room, persistencia ni cálculos financieros complejos en `MainActivity`.

### Restricciones estructurales

- No crees `domain/`, `domain/model/` ni `usecase/` sin autorización explícita.
- No crees `data/local/entity/` solo para aplicar una estructura teórica distinta.
- No muevas archivos existentes únicamente para adoptar otra arquitectura.
- No cambies el paquete raíz `com.rodr.chauchero`.
- Antes de crear una clase, busca si ya existe una responsabilidad equivalente.
- Respeta las subcarpetas reales que ya existan dentro de `data`, `model` y `ui`.

---

## 6. Responsabilidades arquitectónicas

### Capa de datos

- Room es la fuente única de verdad de la información persistente.
- Los DAOs contienen consultas y operaciones de persistencia.
- Los repositorios median entre DAOs y ViewModels.
- Los Composables no acceden directamente a DAOs ni a `AppDatabase`.
- Los ViewModels no deben contener consultas SQL.

### Modelos

- `Gasto`, `PerfilUsuario` y `Prioridad` permanecen en `model`.
- No dupliques los mismos modelos en otro paquete sin una refactorización solicitada.
- No agregues propiedades persistentes que no estén respaldadas por el modelo de datos o por una instrucción explícita.

### Presentación

- Los ViewModels procesan eventos y exponen estado inmutable.
- Los Composables representan el estado y emiten eventos.
- Aplica state hoisting.
- Separa componentes con estado de componentes presentacionales cuando resulte necesario.
- Centraliza la lógica financiera; no repitas las mismas fórmulas en varias pantallas.

---

## 7. Modelo de datos

### `PerfilUsuario`

Debe representar como mínimo:

```text
id_perfil: Int
nombre_perfil: String
salario_fijo: Int
saldo_actual: Int
```

### `Gasto`

Debe representar como mínimo:

```text
id_gasto: Int
id_perfil: Int
nombre_gasto: String
categoria: String
prioridad: Prioridad
valor: Int
estado_pagado: Boolean
```

### `Prioridad`

Valores permitidos:

```text
ALTO
MEDIO
BAJO
```

### Reglas de persistencia

- Relación `PerfilUsuario 1:N Gasto`.
- `Gasto.id_perfil` es clave foránea.
- Mantén integridad referencial con `onDelete = CASCADE`.
- Mantén el índice de la clave foránea cuando Room lo requiera.
- Usa un `TypeConverter` para `Prioridad`.
- Conserva la representación ya utilizada por el converter; no la cambies sin migración.
- Mantén una única instancia de `AppDatabase` por proceso.
- No uses `allowMainThreadQueries()`.
- No uses `fallbackToDestructiveMigration()` sin autorización explícita.
- No borres datos como solución a un cambio de esquema.

### Ambigüedades resueltas

La documentación usa el término “valor dinámico”, pero el modelo entidad-relación no define una columna con ese nombre. Por tanto:

- No agregues una columna `valor_dinamico` por defecto.
- Implementa el comportamiento mediante `valor` y `estado_pagado`.
- Un gasto pagado conserva su valor original, pero queda excluido de la suma pendiente.
- Solo agrega una nueva columna si el desarrollador solicita explícitamente modificar el esquema.

La documentación no define de forma consistente un campo independiente para “onboarding completado”. Por tanto:

- Conserva el mecanismo que ya exista en el repositorio.
- Si todavía no existe ninguno, la presencia del perfil local puede utilizarse para decidir si el onboarding ya fue completado.
- No agregues una columna redundante sin necesidad.

---

## 8. Requisitos funcionales

### RF-01 — Registro de gastos

Permitir registrar un gasto mensual fijo con:

- Nombre.
- Categoría.
- Prioridad.
- Valor monetario en pesos.

El gasto nuevo debe quedar inicialmente en estado pendiente y asociado al perfil local.

### RF-02 — Estado de pago

Permitir alternar un gasto entre:

- Pendiente: `estado_pagado = false`.
- Pagado: `estado_pagado = true`.

### RF-03 — Recálculo reactivo

Cada cambio de estado debe actualizar reactivamente la deuda pendiente.

### RF-04 — Salario fijo

Permitir ingresar y actualizar el saldo líquido mensual o salario fijo.

### RF-05 — Total de gastos fijos

Mostrar la suma de todos los gastos registrados, estén pagados o pendientes.

### RF-06 — Total libre mensual

Calcular el salario fijo menos el total de gastos fijos.

### RF-07 — Saldo bancario actual

Permitir ingresar y actualizar manualmente el saldo real de la cuenta bancaria.

### RF-08 — Por pagar

Calcular la suma exclusiva de los gastos pendientes.

### RF-09 — Libre mensual aproximado

Calcular el saldo bancario actual menos el total por pagar.

### RF-10 — Onboarding y perfil

En la primera ejecución:

- Mostrar las pantallas introductorias.
- Solicitar obligatoriamente el nombre del perfil.
- Permitir ingresar opcionalmente el salario fijo.
- Crear el perfil local.
- Navegar al dashboard.

La edición y eliminación de gastos forman parte del mantenimiento CRUD descrito en los casos de uso, pero impleméntalas solo cuando estén incluidas en la tarea o ya sean parte de la aplicación.

---

## 9. Invariantes financieros

Aplica exactamente estas fórmulas:

```text
totalGastosFijos = SUM(valor de todos los gastos)

totalLibreMensual = salarioFijo - totalGastosFijos

porPagar = SUM(valor de gastos con estado_pagado = false)

libreMensualAproximado = saldoActual - porPagar
```

Reglas obligatorias:

- Un gasto pagado sigue formando parte de `totalGastosFijos`.
- Un gasto pagado se excluye únicamente de `porPagar`.
- Marcar un gasto como pagado no modifica su `valor`.
- Los resultados negativos son válidos y no deben forzarse a cero.
- La UI debe mostrar los resultados negativos con un estado visual de alerta.
- Las consultas agregadas deben devolver `0` cuando no existan filas.

Consultas de referencia:

```sql
SELECT COALESCE(SUM(valor), 0)
FROM Gasto
```

```sql
SELECT COALESCE(SUM(valor), 0)
FROM Gasto
WHERE estado_pagado = 0
```

Adapta los nombres de tabla y columnas a las anotaciones Room reales del repositorio.

---

## 10. Escenarios límite obligatorios

### TC-01 — Salario no configurado

- El salario opcional puede quedar en `0`.
- Los cálculos no deben producir errores nulos.
- Se debe mostrar `$0` o el resultado negativo correspondiente.
- La UI debe invitar a configurar el salario.

### TC-02 — Gastos superiores al salario

- `totalLibreMensual` puede ser negativo.
- La UI debe mostrar el monto en estado de alerta sin romper el diseño.

### TC-03 — Deuda superior al saldo

- `libreMensualAproximado` puede ser negativo.
- No ocultes ni limites matemáticamente el resultado.

### TC-04 — Lista vacía

- `totalGastosFijos = 0`.
- `porPagar = 0`.
- Usa `COALESCE(..., 0)` en agregaciones SQL.

### TC-05 — Todos los gastos pagados

- `porPagar = 0`.
- `libreMensualAproximado = saldoActual`.

### TC-06 — Gasto con valor cero

- Debe poder guardarse.
- Debe cambiar visualmente entre pendiente y pagado.
- No debe alterar los totales.

### TC-07 — Nombres con espacios

- Usa `trim().isNotEmpty()` para validar `nombre_perfil` y `nombre_gasto`.
- Guarda el nombre normalizado sin espacios innecesarios en los extremos.
- Los botones de guardado deben permanecer deshabilitados cuando el nombre sea inválido.

### TC-08 — Desbordamiento monetario

- Acepta únicamente entradas numéricas válidas para los montos.
- Convierte mediante `toIntOrNull()` o una validación equivalente segura.
- No permitas valores mayores que `Int.MAX_VALUE`.
- Limita razonablemente la longitud del campo para evitar desbordamientos.

Los montos ingresados deben ser no negativos. Los resultados calculados sí pueden ser negativos.

---

## 11. Kotlin, Coroutines y Flow

- Evita `!!`.
- Prefiere `val` e inmutabilidad.
- No uses `GlobalScope`.
- No bloquees el hilo principal.
- Usa funciones `suspend` para escrituras de Room.
- Usa `Flow` para lecturas reactivas.
- Usa `viewModelScope` en los ViewModels.
- Mantén los `MutableStateFlow` privados.
- Expón `StateFlow` de solo lectura mediante `asStateFlow()` o una alternativa equivalente.
- No captures excepciones sin tratarlas o comunicarlas.
- Las modificaciones en Room deben reflejarse reactivamente en la UI.
- Usa `Dispatchers.IO` solo cuando exista trabajo bloqueante que Room no gestione de forma adecuada.

---

## 12. Jetpack Compose y navegación

- No crees archivos de vistas XML.
- Usa componentes Material Design 3.
- Respeta los wireframes y la jerarquía visual documentada.
- No inventes pantallas o flujos de navegación.
- Extrae componentes reutilizables a `ui/components` cuando exista reutilización real.
- Mantén el sistema visual en `ui/theme`.
- Evita cálculos financieros complejos dentro de Composables.
- Incluye estados de carga, vacío, validación y error cuando correspondan.
- Evita desbordamientos visuales con montos grandes o negativos.
- Mantén textos y mensajes visibles en español.

Navegación inicial:

- Sin perfil local: onboarding.
- Con perfil local: dashboard o pantalla principal definida por `AppNavigation`.

Después de completar el onboarding, evita que el botón Atrás devuelva accidentalmente al usuario a ese flujo.

---

## 13. Forma de trabajo del agente

### Antes de modificar código

1. Lee este archivo.
2. Lee la documentación relacionada con la tarea.
3. Inspecciona el código existente y sus dependencias.
4. Identifica los `RF`, `CU` y `TC` aplicables.
5. Determina el cambio mínimo necesario.
6. Detecta contradicciones o datos faltantes antes de implementar.

### Durante la implementación

1. Modifica solo los archivos necesarios.
2. Conserva la estructura existente del repositorio.
3. Implementa una funcionalidad verificable por tarea.
4. No realices refactorizaciones masivas fuera del alcance.
5. No elimines código funcional sin justificación.
6. No agregues dependencias innecesarias.
7. No cambies el esquema Room sin revisar versión, migración y compatibilidad.
8. Agrega o actualiza pruebas cuando cambies cálculos, validaciones, DAOs o ViewModels.
9. No incluyas credenciales, claves ni datos personales reales.
10. No hagas commits, pushes, merges ni pull requests salvo solicitud explícita.

### Forma de entregar cambios

Si tienes acceso de escritura al repositorio:

- Modifica directamente los archivos correspondientes.
- Presenta un resumen claro de los cambios.
- Muestra el diff o enumera los archivos modificados.
- No entregues únicamente fragmentos si el cambio también requiere imports, navegación, recursos o Gradle.

Si solo puedes responder mediante chat:

- Para archivos nuevos, entrega el contenido completo.
- Para archivos existentes, entrega bloques reemplazables con ubicación exacta.
- Incluye imports, dependencias y cambios relacionados necesarios.
- No uses fragmentos ambiguos que obliguen al desarrollador a adivinar dónde insertarlos.

---

## 14. Compilación y pruebas

Ejecuta, cuando el entorno lo permita:

```bash
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

En Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

Con emulador o dispositivo disponible:

```bash
./gradlew connectedDebugAndroidTest
```

Valida especialmente:

- Registro de gastos.
- Persistencia después de cerrar y abrir la aplicación.
- Cambio pendiente/pagado.
- Las cuatro fórmulas financieras.
- Lista vacía.
- Todos los gastos pagados.
- Gasto de valor cero.
- Resultados negativos.
- Nombres vacíos o con espacios.
- Entradas monetarias inválidas o demasiado grandes.
- Primera ejecución con y sin salario.
- Inicio posterior con perfil ya creado.
- Navegación hacia atrás después del onboarding.

No afirmes que una compilación o prueba fue exitosa si no se ejecutó correctamente.

---

## 15. Informe final de cada tarea

Al terminar, informa:

1. Resumen de lo implementado.
2. Requisitos `RF`, casos `CU` y escenarios `TC` cubiertos.
3. Archivos creados o modificados.
4. Decisiones técnicas y supuestos realizados.
5. Comandos ejecutados y resultados reales.
6. Pasos para probar manualmente en Android Studio y en un dispositivo Android.
7. Riesgos, limitaciones o trabajo pendiente.

La prioridad es mantener un MVP pequeño, coherente, comprobable y alineado con la documentación y la estructura real del repositorio.
