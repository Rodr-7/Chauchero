package com.rodr.chauchero.ui.viewmodels

import androidx.lifecycle.ViewModel
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
 * Estado de la UI para el Dashboard de Resumen Financiero (CU-05).
 * Centraliza las métricas calculadas en tiempo real (RF-05 al RF-09)
 * y da soporte a los escenarios límite (TC-01, TC-02, TC-03).
 */
data class PresupuestoUiState(
    val nombrePerfil: String = "",
    val salarioFijo: Int = 0,
    val saldoActual: Int = 0,
    val totalGastosFijos: Int = 0,
    val totalLibreMensual: Int = 0,        // RF-06: Salario Fijo - Total Gastos Fijos
    val porPagar: Int = 0,                // RF-08: Suma exclusiva de gastos pendientes
    val libreMensualAproximado: Int = 0,    // RF-09: Saldo Actual - Por Pagar
    val isLoading: Boolean = true
)

/**
 * ViewModel encargado de procesar y exponer reactivamente los cálculos financieros
 * del Dashboard (CU-05) combinando los flujos de PerfilUsuario y Gasto.
 */
class PresupuestoViewModel(
    private val gastoRepository: GastoRepository,
    private val perfilRepository: PerfilUsuarioRepository,
    private val idPerfil: Int = 1 // ID por defecto para el MVP 1.0.0 (Mono-perfil)
) : ViewModel() {

    // Combina de forma reactiva y en tiempo real el perfil y la lista de gastos
    val uiState: StateFlow<PresupuestoUiState> = combine(
        perfilRepository.obtenerPerfilPorId(idPerfil),
        gastoRepository.todosLosGastos
    ) { perfil: PerfilUsuario?, gastos: List<Gasto> ->

        val salarioFijo = perfil?.salarioFijo ?: 0
        val saldoActual = perfil?.saldoActual ?: 0
        val nombrePerfil = perfil?.nombrePerfil ?: "Mi Cuenta"

        // RF-05: Suma total de todos los gastos fijos registrados
        val totalGastosFijos = gastos.sumOf { it.valor }

        // RF-06: Cálculo de proyección ideal / Total libre mensual (Soporta TC-01 y TC-02)
        val totalLibreMensual = salarioFijo - totalGastosFijos

        // RF-08: Cálculo de deuda pendiente ("Por pagar") sumando exclusivamente los pendientes (!estadoPagado)
        val porPagar = gastos.filter { !it.estadoPagado }.sumOf { it.valor }

        // RF-09: Cálculo de flujo de caja real ("Libre mensual aproximado") (Soporta TC-03)
        val libreMensualAproximado = saldoActual - porPagar

        PresupuestoUiState(
            nombrePerfil = nombrePerfil,
            salarioFijo = salarioFijo,
            saldoActual = saldoActual,
            totalGastosFijos = totalGastosFijos,
            totalLibreMensual = totalLibreMensual,
            porPagar = porPagar,
            libreMensualAproximado = libreMensualAproximado,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PresupuestoUiState()
    )

    /**
     * Permite actualizar el salario fijo o el saldo actual bancario (CU-03 / CU-04).
     */
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
}