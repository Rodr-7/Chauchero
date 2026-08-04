# Diagrama de Clases

<aside>
📔

**Versión del Documento:** 1.0.0

**Proyecto:** Chauchero: App de Presupuesto Personal 

**Categoría:** Ahorro y seguimiento de gastos 

</aside>

# **Historial de Versiones**

| **Versión** | **Fecha** | **Autor** | **Cambios Realizados** |
| --- | --- | --- | --- |
| **1.0.0** | 07/2026 | Rodrigo A. | Creación del documento base (). |

# Clases

![MER Chauchero App.svg](Diagrama%20de%20Clases/MER_Chauchero_App.svg)

## Clase Gasto

**Atributos**

- `id_gasto: Int`
- `id_perfil: Int`
- `nombre_gasto: String`
- `categoria: String`
- `prioridad: Enum`
- `valor: Int`
- `estado_pagado: Boolean`

**Métodos (GastoDao)**

- `suspend insertarGasto(gasto: Gasto)`
- `mostrarGasto(id_gasto: Int): Flow<Gasto>`
- `mostrarTodosLosGastos(): Flow<List<Gasto>>`
- `suspend modificarGasto(gasto: Gasto)`
- `suspend borrarGasto(id_gasto: Int)`

## Clase PerfilUsuario

**Atributos**

- `id_perfil: Int`
- `nombre_perfil: String`
- `salario_fijo: Int`
- `saldo_actual: Int`

**Métodos (PerfilUsuarioDao)**

- `suspend insertarPerfil(perfil: PerfilUsuario)`
- `mostrarPerfil(id_perfil: Int): Flow<PerfilUsuario>`
- `suspend modificarPerfil(perfil: PerfilUsuario)`
- `suspend borrarPerfil(id_perfil: Int)`

# Apéndices

### **Sincronización del `TypeConverter` para `prioridad`**

Como la base de datos SQLite no entiende de forma nativa el tipo `Enum` de Kotlin definida en la clase `Gasto` para el atributo `prioridad`, en el código se necesitará programar la clase puente (`TypeConverter`) que traduzca `ALTO`, `MEDIO` y `BAJO` a enteros (`Int`) al escribir en la base de datos y viceversa al leer .
****

Métodos (GastoDao)

- suspend insertarGasto(gasto: Gasto)
- mostrarGasto(id_gasto: Int): Flow<Gasto>
- mostrarTodosLosGastos(): Flow<List<Gasto>>
- suspend modificarGasto(gasto: Gasto)
- suspend borrarGasto(id_gasto: Int)

Métodos (PerfilUsuarioDao)

- suspend insertarPerfil(perfil: PerfilUsuario)
- mostrarPerfil(id_perfil: Int): Flow<PerfilUsuario>
- suspend modificarPerfil(perfil: PerfilUsuario)
- suspend borrarPerfil(id_perfil: Int)

### **Asincronía y Reactividad (Corrutinas y Flow)**

Para garantizar la fluidez de la aplicación y evitar el bloqueo del Hilo Principal (Main Thread), la comunicación con la base de datos SQLite implementa rutinas asíncronas nativas de Kotlin:

- **Operaciones de Escritura (`suspend`):** Los métodos que modifican la base de datos (insertar, modificar, borrar) delegan su trabajo a un hilo secundario en segundo plano.
- **Operaciones de Lectura (`Flow<>`):** Las consultas actúan de forma reactiva. En lugar de solicitar los datos una sola vez, abren un flujo continuo de información; si un registro cambia en la base de datos, el flujo emite la actualización automáticamente hacia la Interfaz de Usuario (UI) para redibujar la pantalla al instante.