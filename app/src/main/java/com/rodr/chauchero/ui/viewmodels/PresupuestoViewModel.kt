package com.rodr.chauchero.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.data.repository.PerfilUsuarioRepository
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.model.PerfilUsuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla Presupuesto (CU-05).
 * Centraliza las métricas calculadas en tiempo real (RF-05 al RF-09)
 * y da soporte a los escenarios límite (TC-01, TC-02, TC-03, TC-04 y TC-05).
 */
data class PresupuestoUiState(
    val nombrePerfil: String = "",
    val salarioFijo: Int = 0,
    val saldoActual: Int = 0,
    val totalGastosFijos: Int = 0,
    val totalLibreMensual: Int = 0,
    val porPagar: Int = 0,
    val libreMensualAproximado: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val proyeccionExpandida: Boolean = true
)

/**
 * ViewModel encargado de procesar eventos y exponer reactivamente los cálculos financieros
 * de Presupuesto combinando los flujos de PerfilUsuario y Gasto.
 */
class PresupuestoViewModel(
    private val gastoRepository: GastoRepository,
    private val perfilRepository: PerfilUsuarioRepository,
    private val idPerfil: Int = 1, // ID por defecto para el MVP 1.0.0 (Mono-perfil)
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    private val proyeccionExpandida = savedStateHandle.getStateFlow(PROYECCION_EXPANDIDA_KEY, true)

    val uiState: StateFlow<PresupuestoUiState> = combine(
        perfilRepository.obtenerPerfilPorId(idPerfil),
        gastoRepository.todosLosGastos,
        proyeccionExpandida
    ) { perfil: PerfilUsuario?, gastos: List<Gasto>, expandida: Boolean ->
        val salarioFijo = perfil?.salarioFijo ?: 0
        val saldoActual = perfil?.saldoActual ?: 0
        val nombrePerfil = perfil?.nombrePerfil ?: "Mi Cuenta"
        val totalGastosFijos = gastos.sumOf { it.valor }
        val totalLibreMensual = salarioFijo - totalGastosFijos
        val porPagar = gastos.filter { !it.estadoPagado }.sumOf { it.valor }
        val libreMensualAproximado = saldoActual - porPagar

        PresupuestoUiState(
            nombrePerfil = nombrePerfil,
            salarioFijo = salarioFijo,
            saldoActual = saldoActual,
            totalGastosFijos = totalGastosFijos,
            totalLibreMensual = totalLibreMensual,
            porPagar = porPagar,
            libreMensualAproximado = libreMensualAproximado,
            isLoading = false,
            proyeccionExpandida = expandida
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PresupuestoUiState()
    )

    fun alternarProyeccionExpandida() {
        savedStateHandle[PROYECCION_EXPANDIDA_KEY] = !proyeccionExpandida.value
    }

    fun actualizarSalario(nuevoSalario: Int) {
        actualizarPerfil(nuevoSalario = nuevoSalario, nuevoSaldo = uiState.value.saldoActual)
    }

    fun actualizarSaldo(nuevoSaldo: Int) {
        actualizarPerfil(nuevoSalario = uiState.value.salarioFijo, nuevoSaldo = nuevoSaldo)
    }

    fun actualizarPerfil(nuevoSalario: Int, nuevoSaldo: Int) {
        viewModelScope.launch {
            val perfilActualizado = PerfilUsuario(
                idPerfil = idPerfil,
                nombrePerfil = uiState.value.nombrePerfil,
                salarioFijo = nuevoSalario,
                saldoActual = nuevoSaldo
            )
            perfilRepository.modificarPerfil(perfilActualizado)
        }
    }

    private companion object {
        const val PROYECCION_EXPANDIDA_KEY = "proyeccion_expandida"
    }
}
