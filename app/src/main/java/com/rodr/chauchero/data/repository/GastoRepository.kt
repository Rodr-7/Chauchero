package com.rodr.chauchero.data.repository

import com.rodr.chauchero.data.local.GastoDao
import com.rodr.chauchero.data.local.CategoriaDao
import com.rodr.chauchero.model.Categoria
import com.rodr.chauchero.model.Gasto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio central para la gestión de datos de la entidad Gasto.
 * Abstrae el acceso a GastoDao para los ViewModels bajo el principio de
 * Single Source of Truth.
 */
class GastoRepository(
    private val gastoDao: GastoDao,
    private val categoriaDao: CategoriaDao
) {

    // Flujo reactivo que expone la lista completa de gastos en tiempo real
    val todosLosGastos: Flow<List<Gasto>> = gastoDao.mostrarTodosLosGastos()

    val todasLasCategorias: Flow<List<Categoria>> = categoriaDao.mostrarTodasLasCategorias()

    suspend fun insertarCategoria(categoria: Categoria): Long {
        return categoriaDao.insertarCategoria(categoria)
    }

    // Obtiene un gasto individual de forma reactiva por su ID
    fun obtenerGastoPorId(idGasto: Int): Flow<Gasto?> {
        return gastoDao.mostrarGasto(idGasto)
    }

    // Inserta un nuevo gasto ejecutándose de forma asíncrona (suspend) en un hilo secundario
    suspend fun insertarGasto(gasto: Gasto) {
        gastoDao.insertarGasto(gasto)
    }

    // Modifica un registro existente (utilizado tanto para editar campos como para CU-02: alternar estado de pago)
    suspend fun modificarGasto(gasto: Gasto) {
        gastoDao.modificarGasto(gasto)
    }

    // Elimina un registro de la base de datos de manera asíncrona
    suspend fun borrarGasto(idGasto: Int) {
        gastoDao.borrarGasto(idGasto)
    }
}
