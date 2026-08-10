package com.rodr.chauchero.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.model.Prioridad
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica de negocio y exposición de estados
 * para el módulo de gestión de gastos (CU-01, CU-02).
 */
class GastosViewModel(
    private val gastoRepository: GastoRepository
) : ViewModel() {

    private val _ordenSeleccionado = MutableStateFlow(OrdenGastos.VALOR_ASCENDENTE)
    val ordenSeleccionado: StateFlow<OrdenGastos> = _ordenSeleccionado.asStateFlow()

    // Combina Room con el criterio seleccionado para ordenar reactivamente.
    val todosLosGastos: StateFlow<List<Gasto>> = combine(
        gastoRepository.todosLosGastos,
        ordenSeleccionado
    ) { gastos, orden -> gastos.ordenadosPor(orden) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun seleccionarOrden(orden: OrdenGastos) {
        _ordenSeleccionado.value = orden
    }

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
                estadoPagado = false
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

enum class OrdenGastos(val etiqueta: String) {
    VALOR_ASCENDENTE("Valor: menor a mayor"),
    VALOR_DESCENDENTE("Valor: mayor a menor"),
    CATEGORIA_ASCENDENTE("Categoría: A-Z"),
    CATEGORIA_DESCENDENTE("Categoría: Z-A"),
    PRIORIDAD_DESCENDENTE("Prioridad: Alto a Bajo"),
    PRIORIDAD_ASCENDENTE("Prioridad: Bajo a Alto")
}

internal fun List<Gasto>.ordenadosPor(orden: OrdenGastos): List<Gasto> {
    val comparator = when (orden) {
        OrdenGastos.VALOR_ASCENDENTE -> compareBy<Gasto> { it.valor }
        OrdenGastos.VALOR_DESCENDENTE -> compareByDescending<Gasto> { it.valor }
        OrdenGastos.CATEGORIA_ASCENDENTE -> compareBy { it.categoria.lowercase() }
        OrdenGastos.CATEGORIA_DESCENDENTE -> compareByDescending { it.categoria.lowercase() }
        OrdenGastos.PRIORIDAD_DESCENDENTE -> compareBy { it.prioridad.ordinal }
        OrdenGastos.PRIORIDAD_ASCENDENTE -> compareByDescending { it.prioridad.ordinal }
    }
    return sortedWith(comparator.thenBy { it.idGasto })
}
