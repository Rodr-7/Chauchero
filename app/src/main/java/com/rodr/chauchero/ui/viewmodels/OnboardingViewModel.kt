package com.rodr.chauchero.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodr.chauchero.data.repository.PerfilUsuarioRepository
import com.rodr.chauchero.model.PerfilUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para el flujo de Onboarding (CU-06).
 */
data class OnboardingUiState(
    val nombrePerfil: String = "",
    val salarioFijoStr: String = "",
    val saldoActualStr: String = "",
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
)

/**
 * ViewModel encargado de procesar la creación del perfil inicial del usuario
 * asegurando validaciones de entrada seguras (TC-07).
 */
class OnboardingViewModel(
    private val perfilRepository: PerfilUsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun actualizarNombre(nombre: String) {
        _uiState.update { it.copy(nombrePerfil = nombre) }
    }

    fun actualizarSalarioFijo(salario: String) {
        _uiState.update { it.copy(salarioFijoStr = salario) }
    }

    fun actualizarSaldoActual(saldo: String) {
        _uiState.update { it.copy(saldoActualStr = saldo) }
    }

    /**
     * Valida y guarda el perfil inicial en la base de datos local usando corrutinas.
     * Cumple con la regla TC-07 (.trim().isNotEmpty()).
     */
    fun guardarPerfilInicial(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val nombreLimpio = currentState.nombrePerfil.trim()

        // Validación estricta según TC-07 (evita nombres vacíos o de puros espacios)
        if (nombreLimpio.isEmpty()) return

        // Conversión segura de enteros con manejo de nulos/vacíos (TC-01 / TC-08)
        val salarioFijo = currentState.salarioFijoStr.trim().toIntOrNull() ?: 0
        val saldoActual = currentState.saldoActualStr.trim().toIntOrNull() ?: 0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val nuevoPerfil = PerfilUsuario(
                nombrePerfil = nombreLimpio,
                salarioFijo = salarioFijo,
                saldoActual = saldoActual
            )

            perfilRepository.insertarPerfil(nuevoPerfil)

            _uiState.update { it.copy(isLoading = false, isCompleted = true) }
            onSuccess()
        }
    }
}