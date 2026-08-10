package com.rodr.chauchero.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import kotlinx.coroutines.flow.map

private sealed interface ProfileStatus {
    data object Loading : ProfileStatus
    data class Ready(val exists: Boolean) : ProfileStatus
}

@Composable
fun ChaucheroNavGraph(
    gastoRepository: GastoRepository,
    perfilRepository: PerfilUsuarioRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String? = null,
) {
    val profileStatus by remember(perfilRepository) {
        perfilRepository.observarPerfilLocal()
            .map { ProfileStatus.Ready(it != null) as ProfileStatus }
    }.collectAsState(initial = ProfileStatus.Loading)

    if ((profileStatus is ProfileStatus.Loading) && (startDestination == null)) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
    }

    val hasProfile = (profileStatus as? ProfileStatus.Ready)?.exists == true
    val destination = startDestination ?: if (!hasProfile) {
        Screen.Onboarding.route
    } else {
        Screen.Dashboard.route
    }
    val bottomDestinations = listOf(
        MainDestination(Screen.Dashboard.route, "Presupuesto", Icons.Default.Home),
        MainDestination(Screen.ListaGastos.route, "Gastos", Icons.AutoMirrored.Filled.List),
        MainDestination(Screen.Ajustes.route, "Ajustes", Icons.Default.Settings)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        bottomDestinations.any { it.route == destination.route }
    } == true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(destination.label) },
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = destination,
            modifier = Modifier.padding(innerPadding)
        ) {
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
                    viewModel = viewModel
                ) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }

            composable(Screen.Dashboard.route) {
                val viewModel: PresupuestoViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PresupuestoViewModel(gastoRepository, perfilRepository) as T
                        }
                    }
                )
                PresupuestoScreen(viewModel = viewModel)
            }

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
                    onNavigateToNuevoGasto = { navController.navigate(Screen.NuevoGasto.route) }
                )
            }

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

            composable(Screen.Ajustes.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ajustes")
                }
            }
        }
    }
}

private data class MainDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)
