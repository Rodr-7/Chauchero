package com.rodr.chauchero.ui.viewmodels

import com.rodr.chauchero.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoriasIndexTest {
    @Test
    fun `indexa categorias por identificador`() {
        val vivienda = Categoria(idCategoria = 1, nombre = "Vivienda", colorHex = "#112233")
        val servicios = Categoria(idCategoria = 2, nombre = "Servicios", colorHex = "#445566")

        val categoriasPorId = listOf(vivienda, servicios).indexadasPorId()

        assertEquals(vivienda, categoriasPorId[1])
        assertEquals(servicios, categoriasPorId[2])
        assertNull(categoriasPorId[3])
    }

    @Test
    fun `lista vacia produce indice vacio`() {
        assertEquals(emptyMap<Int, Categoria>(), emptyList<Categoria>().indexadasPorId())
    }
}
