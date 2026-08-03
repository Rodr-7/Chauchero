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

El código fuente está estrictamente organizado en capas (Data, Domain/Model, Presentation):

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

1. Clona este repositorio en tu máquina local:
    
    ```bash
    git clone https://github.com/tu-usuario/chauchero.git
    ```
    
2. Abre el proyecto en **Android Studio**.
3. Permite que Gradle sincronice las dependencias del proyecto (Room, Compose, Coroutines, etc.).
4. Ejecuta la aplicación en un emulador o dispositivo físico Android (API Level requerido: [Insertar API Mínima, ej. 24+]).

## 🗺️ Roadmap

La versión 1.0.0 contempla el MVP (Producto Mínimo Viable) central. Las siguientes versiones ya documentadas en el Product Backlog incluyen:

- Reinicio Mensual y Gastos temporales
- Implementación de arquitectura Multi-perfil (gestión de múltiples cuentas en un mismo dispositivo).
- Gráficos estadísticos del historial de gastos.
- Implementación de notificaciones locales para vencimientos.

*Diseñado y desarrollado como proyecto de aplicación práctica de Ingeniería de Software.*
