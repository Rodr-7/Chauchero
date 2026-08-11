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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.model.Categoria
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.ui.viewmodels.GastosViewModel
import com.rodr.chauchero.ui.viewmodels.OrdenGastos
import androidx.compose.foundation.shape.RoundedCornerShape

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
    val categoriasPorId by viewModel.categoriasPorId.collectAsState()
    val ordenSeleccionado by viewModel.ordenSeleccionado.collectAsState()
    var menuOrdenAbierto by remember { mutableStateOf(false) }
    val onTogglePagado: (Gasto) -> Unit = remember(viewModel) {
        viewModel::alternarEstadoPago
    }
    val onDelete: (Int) -> Unit = remember(viewModel) {
        viewModel::borrarGasto
    }
    val categoriasVisualesPorId = remember(categoriasPorId) {
        categoriasPorId.mapValues { (_, categoria) -> categoria.toBadgeUiModel() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
        ) {
            Text(
                text = "Lista de Gastos",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp),
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
                    items(
                        items = listaGastos,
                        key = Gasto::idGasto,
                        contentType = { GASTO_ITEM_CONTENT_TYPE }
                    ) { gasto ->
                        GastoItemCard(
                            gasto = gasto,
                            categoria = categoriasVisualesPorId[gasto.idCategoria],
                            onTogglePagado = onTogglePagado,
                            onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(categoria: CategoryBadgeUiModel?) {
    Surface(
        color = categoria?.backgroundColor ?: Color.Gray,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = categoria?.nombre ?: SIN_CATEGORIA,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = categoria?.contentColor ?: Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Immutable
private data class CategoryBadgeUiModel(
    val nombre: String,
    val backgroundColor: Color,
    val contentColor: Color
)

private fun Categoria.toBadgeUiModel(): CategoryBadgeUiModel {
    val backgroundColor = colorHex.toComposeColor()
    return CategoryBadgeUiModel(
        nombre = nombre,
        backgroundColor = backgroundColor,
        contentColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
    )
}

private fun String.toComposeColor(): Color = runCatching {
    Color(android.graphics.Color.parseColor(this))
}.getOrDefault(Color.Gray)

/**
 * Componente Stateless para representar una tarjeta individual de gasto.
 * Incluye la lógica visual para tachar el texto cuando el estado es pagado (CU-02).
 */
@Composable
private fun GastoItemCard(
    gasto: Gasto,
    categoria: CategoryBadgeUiModel?,
    onTogglePagado: (Gasto) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = gasto.estadoPagado,
                onCheckedChange = { onTogglePagado(gasto) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = gasto.nombreGasto,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (gasto.estadoPagado) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    CategoryBadge(categoria = categoria)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${gasto.valor}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (gasto.estadoPagado) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                        textDecoration = if (gasto.estadoPagado) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = "Prioridad: ${gasto.prioridad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            // Botón de eliminación rápida
            IconButton(onClick = { onDelete(gasto.idGasto) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar gasto",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private const val GASTO_ITEM_CONTENT_TYPE = "gasto"
private const val SIN_CATEGORIA = "Sin categoría"
