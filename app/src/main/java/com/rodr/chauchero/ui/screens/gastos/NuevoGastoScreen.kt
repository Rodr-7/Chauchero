package com.rodr.chauchero.ui.screens.gastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.model.Prioridad
import com.rodr.chauchero.ui.viewmodels.GastosViewModel

/**
 * Pantalla de Formulario para Registrar un Nuevo Gasto Fijo (CU-01).
 * Implementa validaciones en tiempo real para evitar campos vacíos o de puros espacios (TC-07).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGastoScreen(
    viewModel: GastosViewModel,
    idPerfil: Int = 1, // Perfil predeterminado para el MVP 1.0.0
    onNavigateBack: () -> Unit
) {
    var nombreGasto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var valorStr by remember { mutableStateOf("") }
    var prioridadSeleccionada by remember { mutableStateOf(Prioridad.MEDIO) }

    // Estado para controlar el menú desplegable (Dropdown) de Prioridades
    var expandedPrioridad by remember { mutableStateOf(false) }

    // Reglas de validación defensiva (TC-07 y conversión segura de número)
    val isNombreValid = nombreGasto.trim().isNotEmpty()
    val isCategoriaValid = categoria.trim().isNotEmpty()
    val valorNumerico = valorStr.trim().toIntOrNull() ?: 0
    val isValorValid = valorNumerico >= 0 && valorStr.trim().isNotEmpty()

    val isFormValid = isNombreValid && isCategoriaValid && isValorValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Nuevo Gasto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("⬅️")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo Nombre del Gasto
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                label = { Text("Nombre del gasto *") },
                singleLine = true,
                isError = nombreGasto.isNotEmpty() && !isNombreValid,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Categoría
            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría (ej. Vivienda, Comida) *") },
                singleLine = true,
                isError = categoria.isNotEmpty() && !isCategoriaValid,
                modifier = Modifier.fillMaxWidth()
            )

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
                        categoria = categoria,
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
}