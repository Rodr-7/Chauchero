# Diagramas de Secuencia

<aside>
📔

**Versión del Documento:** 1.0.0

**Proyecto:** Chauchero: App de Presupuesto Personal 

**Categoría:** Ahorro y seguimiento de gastos 

</aside>

[**Documento de Requisitos de Software (DRS)**](../Documento%20de%20Requisitos%20de%20Software%20(DRS)%203a3677c36f9d8008b569f4fe1d81cf9c.md)

# **Historial de Versiones**

| **Versión** | **Fecha** | **Autor** | **Cambios Realizados** |
| --- | --- | --- | --- |
| **1.0.0** | 07/2026 | Rodrigo A. | Creación del documento base |

| **Caso de Uso** | **Operación Principal** | **Controlador Asociado** | **Método DAO Invocado** |
| --- | --- | --- | --- |
| **CU-02**: Alternar estado de pago | Actualización de estado (`Boolean`) | `GastoDao` | `modificarGasto(gasto: Gasto)` |
| **CU-03 / CU-04**: Actualizar saldos | Modificación de salario o saldo actual | `PerfilUsuarioDao` | `modificarPerfil(perfil: PerfilUsuario)` |
| **CU-06**: Onboarding inicial | Creación del perfil por primera vez | `PerfilUsuarioDao` | `insertarPerfil(perfil: PerfilUsuario)` |

*(Nota de arquitectura: **CU-05 (Visualización de Resumen / Dashboard)** es un flujo de solo lectura y cálculo matemático "al vuelo" que se activa al cargar la pantalla ; por ende, sus actualizaciones se reflejan como resultado inmediato tras cada uno de los siguientes flujos de escritura ).*

---

## CU-01: **Registrar un nuevo gasto fijo**

Este caso de uso implementa el requisito **RF-01**, una de las bases del sistema, la creación de un nuevo Gasto y la definición de cada uno de sus atributos.

### Desglose Cronológico del Flujo

1. **Ingreso de datos (`Usuario` → `Interfaz`):** El usuario ingresa los atributos obligatorios: nombre, categoría, prioridad y valor monetario en pesos. Luego, confirma el guardado.
2. **Construcción del Objeto (`Interfaz`):** La vista empaqueta los datos en una instancia de la clase `Gasto` y le asigna por defecto el estado "pendiente" (`estado_pagado = false`), manteniendo su valor dinámico igual al valor original.
3. **Invocación del DAO (`Interfaz` → `GastoDao`):** La interfaz llama al método `insertarGasto(gasto: Gasto)` enviando el objeto completo en lugar de variables sueltas.
4. **Persistencia Física (`GastoDao` → `Base de Datos`):** El controlador traduce el objeto `Gasto` a una sentencia SQL (`INSERT`) y la ejecuta en el motor SQLite local a través de Room.
5. **Retorno de Confirmación (`Base de Datos` → `GastoDao` → `Interfaz` → `Usuario`):** La base de datos confirma que la fila fue creada exitosamente, el DAO informa a la UI y la pantalla presenta una notificación de éxito al usuario (limpiando el formulario o regresando a la lista).

![Secuencia CU-01 - (Diagrama).svg](Diagramas%20de%20Secuencia/Secuencia_CU-01_-_(Diagrama).svg)

---

## CU-02: Alternar estado de pago de un gasto

Este caso de uso implementa los requisitos **RF-02** y **RF-03** , actualizando visualmente el gasto e impactando la métrica de deuda "Por pagar" .

### Desglose Cronológico del Flujo

1. **Interacción (`Usuario` → `Interfaz`):** El usuario toca la casilla de verificación en una tarjeta de gasto en la lista .
2. **Actualización de Estado (`Interfaz`):** La UI cambia el valor de `estado_pagado` (de `false` a `true` o viceversa) dentro de la instancia de `Gasto` .
3. **Invocación al DAO (`Interfaz` → `GastoDao`):** Se envía la "caja" o entidad completa mediante `modificarGasto(gasto: Gasto)` .
4. **Persistencia y Recálculo (`GastoDao` → `Base de Datos`):** Room ejecuta la sentencia SQL `UPDATE` en SQLite y confirma el cambio .
5. **Retroalimentación (`Interfaz` → `Usuario`):** La interfaz aplica el estilo visual (ej. texto tachado si está pagado) y recalcula la suma "Por pagar" .

![Secuencia CU-02 - (Diagrama).svg](Diagramas%20de%20Secuencia/Secuencia_CU-02_-_(Diagrama).svg)

---

## CU-03 / CU-04: Actualizar Saldo Líquido Mensual y Saldo Bancario

Ambos casos de uso comparten el mismo patrón de interacción técnica: modifican uno de los dos atributos numéricos de entrada en la tabla `PerfilUsuario` (**salario_fijo** o **saldo_actual**) e impactan de forma reactiva las métricas del Dashboard .

### Desglose Cronológico del Flujo

1. **Apertura de Modal/Campo (`Usuario` → `Interfaz`):** El usuario ingresa el nuevo valor de su salario líquido mensual (**RF-04**) o el saldo real en banco (**RF-07**) y confirma .
2. **Empaquetado de Datos (`Interfaz`):** La interfaz actualiza el objeto `PerfilUsuario` con el nuevo monto ingresado .
3. **Invocación al DAO (`Interfaz` → `PerfilUsuarioDao`):** Se llama al método `modificarPerfil(perfil: PerfilUsuario)` con el objeto actualizado .
4. **Persistencia Física (`PerfilUsuarioDao` → `Base de Datos`):** Room ejecuta un `UPDATE` sobre la fila del perfil en SQLite .
5. **Recálculo Reactivo (`Interfaz` → `Usuario`):** Al confirmarse el guardado, la interfaz recalcula al instante las métricas del Módulo 2 ("Total libre mensual" o "Libre mensual aproximado") .

![SecuenciaCU-03 CU-04  (Diagrama).svg](Diagramas%20de%20Secuencia/SecuenciaCU-03_CU-04__(Diagrama).svg)

---

## CU-06: Configuración Inicial de la Cuenta (Onboarding)

Este caso de uso es crítico porque garantiza la **integridad de la base de datos**, creando por primera vez la entidad propietaria (`PerfilUsuario`) a la cual se asociarán los futuros gastos mediante la relación 1:N .

### Desglose Cronológico del Flujo

1. **Ingreso de Datos Iniciales (`Usuario` → `Interfaz`):** Al abrir la app por primera vez, el usuario completa las pantallas informativas, ingresa obligatoriamente el `nombre_perfil` y presiona el botón "Comenzar!" .
2. **Validación Reactiva (`Interfaz`):** La UI valida en tiempo real que el nombre no esté vacío antes de habilitar el botón .
3. **Invocación de Creación (`Interfaz` → `PerfilUsuarioDao`):** La interfaz empaqueta los datos en una nueva instancia y llama a `insertarPerfil(perfil: PerfilUsuario)` .
4. **Inserción en SQLite (`PerfilUsuarioDao` → `Base de Datos`):** Room ejecuta la sentencia `INSERT` y genera el **id_perfil** primario .
5. **Redirección (`Interfaz` → `Usuario`):** Con la cuenta persistida exitosamente, el sistema finaliza el Onboarding y redirige al usuario hacia el Dashboard principal .

![Secuencia CU-06 (Diagrama).svg](Diagramas%20de%20Secuencia/Secuencia_CU-06_(Diagrama).svg)

---

# **Tabla de Interacción Cronológica**

| **Paso ⏱️** | **Origen 📤** | **Destino 📥** | **Acción / Método Ejecutado ⚙️** | **Descripción del Comportamiento en el Sistema 📄** |
| --- | --- | --- | --- | --- |
| **1** | Usuario 👤 | Interfaz 📱 | `completarFormulario()` | El usuario ingresa los atributos obligatorios (`nombre_gasto`, `categoria`, `prioridad`, `valor`) y toca el botón "Guardar". |
| **2** | Interfaz 📱 | Interfaz 📱 | `validarCampos()` | La pantalla verifica en tiempo real que los campos cumplan con las reglas (nombre no vacío y precio como número entero). |
| **2.1 (Alt)** | Interfaz 📱 | Usuario 👤 | `bloquearGuardado()` | **Flujo Alternativo (Campos incompletos):** Si falta algún dato obligatorio, el sistema bloquea la acción y muestra un mensaje de error sin llamar a la base de datos. |
| **3** | Interfaz 📱 | `GastoDao` ⚙️ | `insertarGasto(gasto: Gasto)` | La interfaz instancia el objeto `Gasto` encapsulando todos los atributos y lo envía como parámetro único al controlador. |
| **4** | `GastoDao` ⚙️ | Base de Datos 🗄️ | `INSERT INTO Gasto...` | El controlador ejecuta la consulta de inserción física en la tabla local de Room/SQLite. |
| **5** | Base de Datos 🗄️ | `GastoDao` ⚙️ | `retornoExito` | La base de datos almacena el registro asignando por defecto el estado `"pendiente"` y el valor dinámico igual al valor original, retornando confirmación. |
| **6** | `GastoDao` ⚙️ | Interfaz 📱 | `retornoExito` | El controlador notifica a la pantalla que la operación de escritura finalizó correctamente. |
| **7** | Interfaz 📱 | Usuario 👤 | `mostrarConfirmacion()` | La pantalla se limpia y presenta una notificación visual (ej. *Snackbar* o *Toast*) confirmando la creación exitosa del gasto. |