package com.rodr.chauchero.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.data.repository.PerfilUsuarioRepository
import com.rodr.chauchero.ui.screens.gastos.ListaGastosScreen
import com.rodr.chauchero.ui.screens.gastos.NuevoGastoScreen
import com.rodr.chauchero.ui.screens.onboarding.OnboardingScreen
import com.rodr.chauchero.ui.screens.presupuesto.PresupuestoScreen
import com.rodr.chauchero.ui.viewmodels.GastosViewModel
import com.rodr.chauchero.ui.viewmodels.OnboardingViewModel
import com.rodr.chauchero.ui.viewmodels.PresupuestoViewModel

/**
 * Grafo central de navegación (NavHost) que conecta las pantallas con sus respectivos ViewModels
 * utilizando fábricas personalizadas para la inyección de los repositorios.
 */
@Composable
fun ChaucheroNavGraph(
    gastoRepository: GastoRepository,
    perfilRepository: PerfilUsuarioRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Ruta de Onboarding (CU-06)
        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return OnboardingViewModel(perfilRepository) as T
                    }
                }
            )
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Ruta del Dashboard / Resumen Financiero (CU-05)
        composable(Screen.Dashboard.route) {
            val viewModel: PresupuestoViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return PresupuestoViewModel(gastoRepository, perfilRepository) as T
                    }
                }
            )
            PresupuestoScreen(
                viewModel = viewModel,
                onNavigateToGastos = {
                    navController.navigate(Screen.ListaGastos.route)
                }
            )
        }

        // 3. Ruta del Historial de Gastos (CU-01 / CU-02)
        composable(Screen.ListaGastos.route) {
            val viewModel: GastosViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GastosViewModel(gastoRepository) as T
                    }
                }
            )
            ListaGastosScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNuevoGasto = {
                    navController.navigate(Screen.NuevoGasto.route)
                }
            )
        }

        // 4. Ruta del Formulario de Nuevo Gasto (CU-01)
        composable(Screen.NuevoGasto.route) {
            val viewModel: GastosViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GastosViewModel(gastoRepository) as T
                    }
                }
            )
            NuevoGastoScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}