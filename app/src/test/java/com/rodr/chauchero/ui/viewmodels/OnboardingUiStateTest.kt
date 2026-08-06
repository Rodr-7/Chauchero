package com.rodr.chauchero.ui.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingUiStateTest {

    @Test
    fun `nombre con solo espacios no permite comenzar`() {
        assertFalse(OnboardingUiState(nombrePerfil = "   ").puedeComenzar)
    }

    @Test
    fun `salario vacio es opcional`() {
        assertTrue(OnboardingUiState(nombrePerfil = " Ana ").puedeComenzar)
    }

    @Test
    fun `salario superior a Int maximo no permite comenzar`() {
        val state = OnboardingUiState(nombrePerfil = "Ana", salarioFijoStr = "2147483648")

        assertFalse(state.puedeComenzar)
    }

    @Test
    fun `entrada monetaria conserva solo diez digitos`() {
        assertEquals("1234567890", normalizarMontoInput("$ 1.234.567.89099"))
    }
}
