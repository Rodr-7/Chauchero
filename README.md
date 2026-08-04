# Chauchero 🐷💰

**App de Presupuesto Personal y Seguimiento de Gastos para Android**

Chauchero es una aplicación nativa para Android diseñada para ofrecer un control sobre tus finanzas personales. Al registrar tus gastos e ingresos fijos mensuales, Chauchero calcula y muestra tu liquidez mensual real y tu deuda pendiente, adaptándose dinámicamente a medida que marcas tus gastos mensuales fijos como "pagados" o "pendientes".

Esta App surgió a partir de un sistema casero de gestión de mis finanzas personales basado en hojas de cálculo de Notion, Chauchero traslada esa lógica a un entorno móvil profesional, rápido y completamente reactivo.

## ✨ Características Principales

- **Gestión de Gastos Fijos:** Registra tus gastos mensuales asignándoles nombre, categoría, prioridad (ALTO, MEDIO, BAJO) y valor monetario.
- **Cálculo Reactivo de Deuda:** Utiliza un sistema de *checkbox* para alternar el estado de un gasto entre "Pagado" y "Pendiente". El sistema recalcula la métrica "Por pagar" automáticamente en segundo plano.
- **Proyección Financiera:** Calcula el "Total libre mensual" (Salario Fijo - Total de Gastos) y el "Libre mensual aproximado" (Saldo Bancario Actual - Total Por Pagar).
- **Onboarding y Perfiles:** Flujo de primer inicio para configurar el nombre de tu cuenta y tu salario base inicial.
- **Diseño Moderno:** Interfaz de usuario declarativa, minimalista y basada en los estándares de Material Design.

## 🛠️ Pila Tecnológica y Arquitectura

Este proyecto fue construido aplicando rigurosos estándares de ingeniería de software, priorizando la mantenibilidad, escalabilidad y la prevención del *Feature Creep*.

- **Plataforma:** Android (Nativo)
- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose (Diseño 100% declarativo)
- **Arquitectura:** MVVM (Model-View-ViewModel) con separación de responsabilidades (Hoisting).
- **Base de Datos Local:** Room Database (SQLite) operando como *Single Source of Truth*.
- **Asincronismo y Reactividad:** Kotlin Coroutines y StateFlow / Flow para una UI siempre sincronizada con la base de datos sin bloquear el hilo principal.

## 🗂️ Estructura del Proyecto

El código fuente está organizado según la estructura real del paquete `com.rodr.chauchero`, separando datos, modelos y presentación:

```
com.rodr.chauchero
│
├── data/                   # CAPA DE DATOS (Single Source of Truth)
│   ├── local/              # Base de datos Room (AppDatabase, Converters, DAOs)
│   └── repository/         # Abstracción y acceso único a los datos
│
├── model/                  # MODELOS DE DOMINIO
│   ├── Gasto.kt            # Entidad Gasto
│   ├── PerfilUsuario.kt    # Entidad PerfilUsuario
│   └── Prioridad.kt        # Enum (ALTO, MEDIO, BAJO)
│
└── ui/                     # CAPA DE PRESENTACIÓN (UI)
    ├── theme/              # Colores, tipografía y formas
    ├── components/         # Bloques visuales reutilizables (Stateless)
    ├── screens/            # Vistas completas por módulo (Stateful)
    └── viewmodels/         # Controladores de estado reactivo
```

---

## 🚀 Instalación y Ejecución

### Requisitos generales

Para clonar, abrir, sincronizar, compilar y ejecutar Chauchero desde Windows, Linux o macOS necesitas:

- Android Studio estable compatible con Android Gradle Plugin 8.5.x.
- JDK 17 configurado como **Gradle JDK** en Android Studio.
- Android SDK instalado localmente en cada computador.
- SDK Platform Android 14, API 34.
- Android SDK Build Tools instaladas desde Android Studio.
- Emulador Android o dispositivo físico con Android 7.0, API 24, o superior.
- Conexión a internet durante la primera sincronización para descargar Gradle, plugins y dependencias.

> No configures rutas absolutas personales dentro de archivos versionados del repositorio.

---

### Archivos que deben estar en Git

Estos archivos sí deben permanecer versionados:

```text
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
gradle.properties
gradle/libs.versions.toml
gradle/wrapper/gradle-wrapper.properties
gradle/wrapper/gradle-wrapper.jar
gradlew
gradlew.bat
app/src/main/AndroidManifest.xml
README.md
.gitignore
```

El Gradle Wrapper es obligatorio para que todos usen la misma versión de Gradle sin depender de una instalación global.

---

### Archivos que NO deben subirse a Git

Estos archivos o carpetas deben ser locales de cada computador:

```text
local.properties
.gradle/
build/
app/build/
.idea/
.kotlin/
.cxx/
captures/
*.iml
```

Especialmente, `local.properties` no debe versionarse porque contiene la ruta local del Android SDK de cada persona.

---

### Clonar el repositorio

Usa la URL real del repositorio:

```bash
git clone <URL_REAL_DEL_REPOSITORIO>
cd Chauchero
```

---

### Abrir en Android Studio

1. Abre Android Studio.
2. Selecciona **Open**.
3. Elige la carpeta raíz del repositorio, no la carpeta `app`.
4. Espera a que Android Studio detecte el proyecto Gradle.
5. Configura **Gradle JDK** en JDK 17:
   - Windows/Linux: **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
   - macOS: **Android Studio > Settings > Build, Execution, Deployment > Build Tools > Gradle**.
6. Ejecuta **Sync Project with Gradle Files**.

---

### Configurar Android SDK

Desde Android Studio abre **SDK Manager** e instala:

- Android SDK Platform 34.
- Android SDK Build Tools.
- Android SDK Platform-Tools.
- Android Emulator, si usarás emulador.
- Un system image para crear un AVD, por ejemplo API 34.

La aplicación tiene:

```text
compileSdk = 34
targetSdk = 34
minSdk = 24
```

Por lo tanto:

- Para compilar necesitas SDK Platform 34.
- Para ejecutar necesitas un emulador o dispositivo con API 24 o superior.

---

### Configurar `local.properties`

Android Studio normalmente genera `local.properties` automáticamente al abrir el proyecto.

Si necesitas crearlo manualmente, debe quedar en la raíz del repositorio y contener solo la ruta local del Android SDK.

Ejemplo en Windows:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

Ejemplo en macOS:

```properties
sdk.dir=/Users/TU_USUARIO/Library/Android/sdk
```

Ejemplo en Linux:

```properties
sdk.dir=/home/TU_USUARIO/Android/Sdk
```

No subas este archivo a Git.

---

### Permisos de `gradlew` en Linux/macOS

Si después de clonar el repositorio `gradlew` no tiene permisos de ejecución, ejecútalo una vez:

```bash
chmod +x gradlew
```

Después valida:

```bash
./gradlew --version
```

---

### Validar desde terminal

Linux/macOS:

```bash
./gradlew --version
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat --version
gradlew.bat test
gradlew.bat lint
gradlew.bat assembleDebug
```

---

### Ejecutar la app

1. Crea o selecciona un emulador Android con API 24 o superior.
2. También puedes conectar un dispositivo físico con depuración USB habilitada.
3. Selecciona la configuración `app` en Android Studio.
4. Presiona **Run**.

La app funciona localmente y no requiere servicios web, Firebase, Retrofit ni conexión con bancos.

## 🗺️ Roadmap

La versión 1.0.0 contempla el MVP (Producto Mínimo Viable) central. Las siguientes versiones ya documentadas en el Product Backlog incluyen:

- Reinicio Mensual y Gastos temporales
- Implementación de arquitectura Multi-perfil (gestión de múltiples cuentas en un mismo dispositivo).
- Gráficos estadísticos del historial de gastos.
- Implementación de notificaciones locales para vencimientos.

*Diseñado y desarrollado como proyecto de aplicación práctica de Ingeniería de Software.*
