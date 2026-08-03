package com.rodr.chauchero.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.model.Prioridad
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica de negocio y exposición de estados
 * para el módulo de gestión de gastos (CU-01, CU-02).
 */
class GastosViewModel(
    private val gastoRepository: GastoRepository
) : ViewModel() {

    // Convierte el Flow de la base de datos en un StateFlow reactivo y seguro para Jetpack Compose
    val todosLosGastos: StateFlow<List<Gasto>> = gastoRepository.todosLosGastos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Registra un nuevo gasto fijo aplicando validaciones defensivas de entrada (TC-07).
     * Cumple con el flujo principal del Caso de Uso CU-01.
     */
    fun registrarGasto(
        idPerfil: Int,
        nombreGasto: String,
        categoria: String,
        prioridad: Prioridad,
        valor: Int
    ) {
        val nombreLimpio = nombreGasto.trim()

        // Validación estricta: bloquea nombres vacíos o montos negativos (TC-07)
        if (nombreLimpio.isEmpty() || valor < 0) return

        viewModelScope.launch {
            val nuevoGasto = Gasto(
                idPerfil = idPerfil,
                nombreGasto = nombreLimpio,
                categoria = categoria.trim(),
                prioridad = prioridad,
                valor = valor,
                estadoPagado = false // Todo gasto nace por defecto en estado "pendiente" (CU-01)
            )
            gastoRepository.insertarGasto(nuevoGasto)
        }
    }

    /**
     * Alterna el estado de pago de un gasto (CU-02 / RF-02 / RF-03).
     * Al cambiar a pagado o pendiente, desencadena el recálculo automático de la deuda.
     */
    fun alternarEstadoPago(gasto: Gasto) {
        viewModelScope.launch {
            val gastoModificado = gasto.copy(estadoPagado = !gasto.estadoPagado)
            gastoRepository.modificarGasto(gastoModificado)
        }
    }

    /**
     * Elimina un gasto de la base de datos de forma asíncrona.
     */
    fun borrarGasto(idGasto: Int) {
        viewModelScope.launch {
            gastoRepository.borrarGasto(idGasto)
        }
    }
}