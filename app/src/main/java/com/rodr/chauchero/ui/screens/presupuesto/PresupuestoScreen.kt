package com.rodr.chauchero.ui.screens.presupuesto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.ui.viewmodels.PresupuestoViewModel

/**
 * Pantalla principal del Dashboard (Resumen Financiero - CU-05).
 * Presenta las métricas en tiempo real (RF-05 al RF-09) conectadas con PresupuestoViewModel,
 * gestionando de forma declarativa las alertas de casos límite (TC-01, TC-02, TC-03).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoScreen(
    viewModel: PresupuestoViewModel,
    onNavigateToGastos: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chauchero 🐷: ${uiState.nombrePerfil}") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToGastos) {
                Text("📋")
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Proyección Ideal (RF-05, RF-06)
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Proyección del Ciclo", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Salario Fijo: $${uiState.salarioFijo}")
                            Text("Total Gastos Fijos: $${uiState.totalGastosFijos}")

                            Spacer(modifier = Modifier.height(8.dp))

                            // TC-02: Si los gastos superan los ingresos, se renderiza en color rojo de alerta
                            val isNegativeProyeccion = uiState.totalLibreMensual < 0
                            Text(
                                text = "Total Libre Mensual: $${uiState.totalLibreMensual}",
                                color = if (isNegativeProyeccion) Color.Red else MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Tarjeta 2: Flujo de Caja Real y Deuda Pendiente (RF-08, RF-09)
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Flujo de Caja Real", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Saldo Bancario Actual: $${uiState.saldoActual}")
                            Text("Por Pagar (Pendientes): $${uiState.porPagar}", color = MaterialTheme.colorScheme.error)

                            Spacer(modifier = Modifier.height(8.dp))

                            // TC-03: Si la deuda pendiente supera el saldo disponible
                            val isNegativeFlujo = uiState.libreMensualAproximado < 0
                            Text(
                                text = "Libre Mensual Aproximado: $${uiState.libreMensualAproximado}",
                                color = if (isNegativeFlujo) Color.Red else Color(0xFF2E7D32), // Verde corporativo
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}