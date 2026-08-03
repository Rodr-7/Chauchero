package com.rodr.chauchero.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.ui.viewmodels.OnboardingViewModel

/**
 * Pantalla de Configuración Inicial (Onboarding - CU-06).
 * Solicita el nombre del perfil y opcionalmente el salario fijo,
 * aplicando validaciones defensivas (TC-07).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Regla TC-07: El botón de comenzar solo se habilita si el nombre tiene texto real tras hacer trim()
    val isNombreValid = uiState.nombrePerfil.trim().isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido a Chauchero 🐷💰") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configura tu cuenta inicial",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Organiza tus finanzas personales de forma simple y reactiva.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de texto para el nombre del perfil (Obligatorio)
            OutlinedTextField(
                value = uiState.nombrePerfil,
                onValueChange = { viewModel.actualizarNombre(it) },
                label = { Text("Nombre de la cuenta / perfil *") },
                singleLine = true,
                isError = uiState.nombrePerfil.isNotEmpty() && !isNombreValid,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para el Salario Fijo (Opcional - Soporta TC-01)
            OutlinedTextField(
                value = uiState.salarioFijoStr,
                onValueChange = { viewModel.actualizarSalarioFijo(it) },
                label = { Text("Salario Fijo Mensual (Opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para el Saldo Actual Bancario (Opcional)
            OutlinedTextField(
                value = uiState.saldoActualStr,
                onValueChange = { viewModel.actualizarSaldoActual(it) },
                label = { Text("Saldo Bancario Actual (Opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de acción principal
            Button(
                onClick = {
                    viewModel.guardarPerfilInicial {
                        onNavigateToDashboard()
                    }
                },
                enabled = isNombreValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Comenzar")
                }
            }
        }
    }
}