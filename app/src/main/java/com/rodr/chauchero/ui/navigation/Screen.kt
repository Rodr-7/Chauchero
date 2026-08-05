package com.rodr.chauchero.ui.navigation

/**
 * Definición central de rutas para el sistema de navegación.
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object ListaGastos : Screen("lista_gastos")
    object NuevoGasto : Screen("nuevo_gasto")
    object Ajustes : Screen("ajustes")
}
