package com.rodr.chauchero.ui.screens.gastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.ui.viewmodels.GastosViewModel

/**
 * Pantalla de Listado e Historial de Gastos (CU-01 / CU-02).
 * Permite visualizar los gastos registrados, alternar su estado de pago mediante checkboxes (CU-02),
 * y activar los recálculos automáticos de deuda en segundo plano (RF-03).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaGastosScreen(
    viewModel: GastosViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNuevoGasto: () -> Unit
) {
    val listaGastos by viewModel.todosLosGastos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Gastos Fijos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("⬅️")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNuevoGasto) {
                Text("➕")
            }
        }
    ) { innerPadding ->
        if (listaGastos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay gastos registrados aún. ¡Añade uno!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaGastos, key = { it.idGasto }) { gasto ->
                    GastoItemCard(
                        gasto = gasto,
                        onTogglePagado = { viewModel.alternarEstadoPago(gasto) },
                        onDelete = { viewModel.borrarGasto(gasto.idGasto) }
                    )
                }
            }
        }
    }
}

/**
 * Componente Stateless para representar una tarjeta individual de gasto.
 * Incluye la lógica visual para tachar el texto cuando el estado es pagado (CU-02).
 */
@Composable
fun GastoItemCard(
    gasto: Gasto,
    onTogglePagado: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = gasto.estadoPagado,
                    onCheckedChange = { onTogglePagado() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = gasto.nombreGasto,
                        style = MaterialTheme.typography.titleMedium,
                        // Si está pagado, se muestra tachado visualmente cumpliendo CU-02
                        textDecoration = if (gasto.estadoPagado) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = "Categoría: ${gasto.categoria} | Prioridad: ${gasto.prioridad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Valor: $${gasto.valor}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Botón de eliminación rápida
            IconButton(onClick = onDelete) {
                Text("🗑️")
            }
        }
    }
}