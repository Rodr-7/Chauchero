package com.rodr.chauchero.ui.screens.presupuesto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.ui.theme.ChaucheroGreen
import com.rodr.chauchero.ui.theme.ChaucheroTheme
import com.rodr.chauchero.ui.theme.ChaucheroWarning
import com.rodr.chauchero.ui.viewmodels.PresupuestoUiState
import com.rodr.chauchero.ui.viewmodels.PresupuestoViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PresupuestoScreen(
    viewModel: PresupuestoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    PresupuestoContent(
        uiState = uiState,
        onActualizarPresupuesto = viewModel::actualizarSalario,
        onActualizarSaldo = viewModel::actualizarSaldo,
        onAlternarProyeccion = viewModel::alternarProyeccionExpandida,
        modifier = modifier
    )
}

@Composable
fun PresupuestoContent(
    uiState: PresupuestoUiState,
    onActualizarPresupuesto: (Int) -> Unit,
    onActualizarSaldo: (Int) -> Unit,
    onAlternarProyeccion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showBalanceDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 0.dp,
                    bottom = 100.dp // Espacio para el botón fijo
                )
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Chauchero de ${uiState.nombrePerfil}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tu Presupuesto",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                item {
                    ResumenFinancieroCard(uiState = uiState)
                }

                item {
                    ProyeccionGastosCard(
                        uiState = uiState,
                        expanded = uiState.proyeccionExpandida,
                        onExpandedChange = onAlternarProyeccion,
                        onActualizarPresupuesto = { showBudgetDialog = true }
                    )
                }
            }

            // Botón fijo en la parte inferior
            Button(
                onClick = { showBalanceDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChaucheroGreen)
            ) {
                Text(
                    text = "Actualizar Saldo",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showBudgetDialog) {
        MoneyInputDialog(
            title = "Actualizar presupuesto",
            label = "Ingresos mensuales",
            initialValue = uiState.salarioFijo,
            onDismiss = { showBudgetDialog = false },
            onConfirm = {
                onActualizarPresupuesto(it)
                showBudgetDialog = false
            }
        )
    }

    if (showBalanceDialog) {
        MoneyInputDialog(
            title = "Actualizar saldo",
            label = "Saldo actual",
            initialValue = uiState.saldoActual,
            onDismiss = { showBalanceDialog = false },
            onConfirm = {
                onActualizarSaldo(it)
                showBalanceDialog = false
            }
        )
    }
}

@Composable
private fun ResumenFinancieroCard(uiState: PresupuestoUiState) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val libreColor = if (uiState.libreMensualAproximado >= 0) ChaucheroGreen else ChaucheroWarning
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Saldo libre", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = formatPesos(uiState.libreMensualAproximado),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = libreColor
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricText("Por pagar", uiState.porPagar)
                MetricText("Saldo actual", uiState.saldoActual)
            }
        }
    }
}

@Composable
private fun ProyeccionGastosCard(
    uiState: PresupuestoUiState,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onActualizarPresupuesto: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proyección de gastos mensuales",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = onExpandedChange,
                modifier = Modifier.semantics { contentDescription = "Expandir o contraer proyección de gastos" }
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MetricText("Total de gastos fijos", uiState.totalGastosFijos)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(0.86f)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Ingresos mensuales", style = MaterialTheme.typography.bodySmall)
                            Text(formatPesos(uiState.salarioFijo), style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = onActualizarPresupuesto,
                                modifier = Modifier.sizeIn(minHeight = 40.dp),
                                contentPadding = ButtonDefaults.ContentPadding
                            ) {
                                Text("Actualizar\nPresupuesto", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("Total libre mensual:", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = formatPesos(uiState.totalLibreMensual),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.totalLibreMensual < 0) ChaucheroWarning else Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricText(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Text(formatPesos(value), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun MoneyInputDialog(
    title: String,
    label: String,
    initialValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember(initialValue) { mutableStateOf(initialValue.takeIf { it > 0 }?.toString().orEmpty()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val parsedValue = value.toIntOrNull()
    val isValid = value.isNotBlank() && parsedValue != null && parsedValue >= 0

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 9 && newValue.all { it.isDigit() }) {
                            value = newValue
                        }
                    },
                    label = { Text(label) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = value.isNotBlank() && parsedValue == null,
                    supportingText = {
                        Text(
                            text = if (value.isBlank()) {
                                "Ingresa un monto en pesos chilenos."
                            } else if (parsedValue == null) {
                                "El monto debe ser numérico y válido."
                            } else {
                                "Máximo 9 dígitos."
                            }
                        )
                    },
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { parsedValue?.let(onConfirm) },
                enabled = isValid
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatPesos(value: Int): String {
    val formatter = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    val sign = if (value < 0) "-" else ""
    return "$sign$ ${formatter.format(kotlin.math.abs(value))}"
}

@Preview(showBackground = true)
@Composable
private fun PresupuestoContentPreview() {
    ChaucheroTheme {
        PresupuestoContent(
            uiState = PresupuestoUiState(
                nombrePerfil = "Perfil1",
                salarioFijo = 500000,
                saldoActual = 150000,
                totalGastosFijos = 200000,
                totalLibreMensual = 300000,
                porPagar = 30000,
                libreMensualAproximado = 120000,
                isLoading = false
            ),
            onActualizarPresupuesto = {},
            onActualizarSaldo = {},
            onAlternarProyeccion = {}
        )
    }
}
