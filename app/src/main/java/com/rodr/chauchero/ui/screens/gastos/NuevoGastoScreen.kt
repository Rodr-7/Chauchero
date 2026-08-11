package com.rodr.chauchero.ui.screens.gastos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.model.Prioridad
import com.rodr.chauchero.ui.viewmodels.GastosViewModel

/**
 * Pantalla de Formulario para Registrar un Nuevo Gasto Fijo (CU-01).
 * Implementa validaciones en tiempo real para evitar campos vacíos o de puros espacios (TC-07).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NuevoGastoScreen(
    viewModel: GastosViewModel,
    idPerfil: Int = 1, // Perfil predeterminado para el MVP 1.0.0
    onNavigateBack: () -> Unit
) {
    var nombreGasto by remember { mutableStateOf("") }
    val categorias by viewModel.categorias.collectAsState()
    val errorCategoria by viewModel.errorCategoria.collectAsState()
    var idCategoriaSeleccionada by rememberSaveable { mutableStateOf<Int?>(null) }
    var mostrarDialogoCategoria by rememberSaveable { mutableStateOf(false) }
    var valorStr by remember { mutableStateOf("") }
    var prioridadSeleccionada by remember { mutableStateOf(Prioridad.MEDIO) }
    val nombreFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Estado para controlar el menú desplegable (Dropdown) de Prioridades
    var expandedPrioridad by remember { mutableStateOf(false) }

    // Reglas de validación defensiva (TC-07 y conversión segura de número)
    val isNombreValid = nombreGasto.trim().isNotEmpty()
    val valorNumerico = valorStr.trim().toIntOrNull() ?: 0
    val isValorValid = valorNumerico >= 0 && valorStr.trim().isNotEmpty()

    val isFormValid = isNombreValid && idCategoriaSeleccionada != null && isValorValid

    LaunchedEffect(Unit) {
        nombreFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Lista de Gastos > Nuevo Gasto",
                modifier = Modifier.padding(top = 0.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Campo Nombre del Gasto
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                label = { Text("Nombre del gasto *") },
                singleLine = true,
                isError = nombreGasto.isNotEmpty() && !isNombreValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nombreFocusRequester)
            )

            Text("Categoría *", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    CategoriaTag(
                        nombre = categoria.nombre,
                        color = categoria.colorHex.toComposeColor(),
                        seleccionada = categoria.idCategoria == idCategoriaSeleccionada,
                        onClick = { idCategoriaSeleccionada = categoria.idCategoria }
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { mostrarDialogoCategoria = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir categoría")
                    }
                }
            }
            if (categorias.isEmpty()) {
                Text(
                    "Añade una categoría para continuar.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Selector de Prioridad (ExposedDropdownMenu Box)
            ExposedDropdownMenuBox(
                expanded = expandedPrioridad,
                onExpandedChange = { expandedPrioridad = !expandedPrioridad },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = prioridadSeleccionada.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPrioridad,
                    onDismissRequest = { expandedPrioridad = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(prioridad.name) },
                            onClick = {
                                prioridadSeleccionada = prioridad
                                expandedPrioridad = false
                            }
                        )
                    }
                }
            }

            // Campo Valor Monetario
            OutlinedTextField(
                value = valorStr,
                onValueChange = { valorStr = it },
                label = { Text("Valor en pesos ($) *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = valorStr.isNotEmpty() && !isValorValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Confirmación / Guardado (CU-01)
            Button(
                onClick = {
                    viewModel.registrarGasto(
                        idPerfil = idPerfil,
                        nombreGasto = nombreGasto,
                        idCategoria = idCategoriaSeleccionada ?: return@Button,
                        prioridad = prioridadSeleccionada,
                        valor = valorNumerico
                    )
                    onNavigateBack()
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Gasto")
            }
        }
    }

    if (mostrarDialogoCategoria) {
        NuevaCategoriaDialog(
            onDismiss = { mostrarDialogoCategoria = false },
            onConfirm = { nombre, colorHex ->
                viewModel.agregarCategoria(nombre, colorHex)
                mostrarDialogoCategoria = false
            }
        )
    }
    if (errorCategoria != null) {
        AlertDialog(
            onDismissRequest = viewModel::limpiarErrorCategoria,
            text = { Text(errorCategoria.orEmpty()) },
            confirmButton = {
                TextButton(onClick = viewModel::limpiarErrorCategoria) { Text("Aceptar") }
            }
        )
    }
}

@Composable
private fun CategoriaTag(
    nombre: String,
    color: Color,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = color,
        border = if (seleccionada) {
            androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface)
        } else null
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(nombre, color = if (color.luminance() > 0.5f) Color.Black else Color.White)
        }
    }
}

@Composable
private fun NuevaCategoriaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var colorHex by rememberSaveable { mutableStateOf("#6750A4") }
    val nombreValido = nombre.trim().isNotEmpty()
    val colorValido = colorHex.trim().matches(Regex("^#[0-9a-fA-F]{6}$"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = colorHex,
                    onValueChange = { if (it.length <= 7) colorHex = it },
                    label = { Text("Color hexadecimal *") },
                    supportingText = { Text("Formato: #RRGGBB") },
                    isError = colorHex.isNotEmpty() && !colorValido,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nombre, colorHex) },
                enabled = nombreValido && colorValido
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun String.toComposeColor(): Color = runCatching {
    Color(android.graphics.Color.parseColor(this))
}.getOrDefault(Color.Gray)
