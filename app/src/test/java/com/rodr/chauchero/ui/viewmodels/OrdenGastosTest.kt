package com.rodr.chauchero.ui.viewmodels

import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.model.Prioridad
import org.junit.Assert.assertEquals
import org.junit.Test

class OrdenGastosTest {
    private val gastos = listOf(
        gasto(1, "Servicios", Prioridad.MEDIO, 20),
        gasto(2, "arriendo", Prioridad.ALTO, 30),
        gasto(3, "Comida", Prioridad.BAJO, 10)
    )

    @Test
    fun `ordena valor en ambas direcciones`() {
        assertEquals(listOf(3, 1, 2), gastos.ordenadosPor(OrdenGastos.VALOR_ASCENDENTE).ids())
        assertEquals(listOf(2, 1, 3), gastos.ordenadosPor(OrdenGastos.VALOR_DESCENDENTE).ids())
    }

    @Test
    fun `ordena categoria ignorando mayusculas`() {
        assertEquals(listOf(2, 3, 1), gastos.ordenadosPor(OrdenGastos.CATEGORIA_ASCENDENTE).ids())
        assertEquals(listOf(1, 3, 2), gastos.ordenadosPor(OrdenGastos.CATEGORIA_DESCENDENTE).ids())
    }

    @Test
    fun `ordena prioridad según jerarquia`() {
        assertEquals(listOf(2, 1, 3), gastos.ordenadosPor(OrdenGastos.PRIORIDAD_DESCENDENTE).ids())
        assertEquals(listOf(3, 1, 2), gastos.ordenadosPor(OrdenGastos.PRIORIDAD_ASCENDENTE).ids())
    }

    private fun gasto(id: Int, categoria: String, prioridad: Prioridad, valor: Int) = Gasto(
        idGasto = id,
        idPerfil = 1,
        nombreGasto = "Gasto $id",
        categoria = categoria,
        prioridad = prioridad,
        valor = valor
    )

    private fun List<Gasto>.ids() = map { it.idGasto }
}
