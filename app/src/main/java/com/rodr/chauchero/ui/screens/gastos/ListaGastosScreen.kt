package com.rodr.chauchero.ui.screens.gastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.ui.viewmodels.GastosViewModel
import com.rodr.chauchero.ui.viewmodels.OrdenGastos

/**
 * Pantalla de Listado e Historial de Gastos (CU-01 / CU-02).
 * Permite visualizar los gastos registrados, alternar su estado de pago mediante checkboxes (CU-02),
 * y activar los recálculos automáticos de deuda en segundo plano (RF-03).
 */
@Composable
fun ListaGastosScreen(
    viewModel: GastosViewModel,
    onNavigateToNuevoGasto: () -> Unit
) {
    val listaGastos by viewModel.todosLosGastos.collectAsState()
    val ordenSeleccionado by viewModel.ordenSeleccionado.collectAsState()
    var menuOrdenAbierto by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                Box {
                    SmallFloatingActionButton(onClick = { menuOrdenAbierto = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Ordenar gastos")
                    }
                    DropdownMenu(
                        expanded = menuOrdenAbierto,
                        onDismissRequest = { menuOrdenAbierto = false }
                    ) {
                        OrdenGastos.entries.forEach { orden ->
                            DropdownMenuItem(
                                text = { Text(orden.etiqueta) },
                                onClick = {
                                    viewModel.seleccionarOrden(orden)
                                    menuOrdenAbierto = false
                                },
                                leadingIcon = {
                                    RadioButton(selected = orden == ordenSeleccionado, onClick = null)
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                FloatingActionButton(onClick = onNavigateToNuevoGasto) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir gasto")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            Text(
                text = "Lista de Gastos",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(18.dp))

            if (listaGastos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay gastos registrados aún. ¡Añade uno!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
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
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar gasto",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
