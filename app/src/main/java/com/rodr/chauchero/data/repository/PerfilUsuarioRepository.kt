package com.rodr.chauchero.data.repository

import com.rodr.chauchero.data.local.PerfilUsuarioDao
import com.rodr.chauchero.model.PerfilUsuario
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio encargado de gestionar la persistencia y recuperación
 * de la entidad PerfilUsuario (salario fijo, saldo actual y nombre del perfil).
 */
class PerfilUsuarioRepository(private val perfilUsuarioDao: PerfilUsuarioDao) {

    // Obtiene el perfil de manera reactiva en tiempo real por su ID
    fun obtenerPerfilPorId(idPerfil: Int): Flow<PerfilUsuario?> {
        return perfilUsuarioDao.mostrarPerfil(idPerfil)
    }

    // Inserta el perfil inicial de la aplicación (utilizado en el flujo de Onboarding CU-06)
    suspend fun insertarPerfil(perfil: PerfilUsuario) {
        perfilUsuarioDao.insertarPerfil(perfil)
    }

    // Actualiza los datos del perfil (como salario fijo o saldo actual bancario)
    suspend fun modificarPerfil(perfil: PerfilUsuario) {
        perfilUsuarioDao.modificarPerfil(perfil)
    }

    // Elimina el perfil de usuario (disparando la eliminación en cascada de los gastos asociados)
    suspend fun borrarPerfil(idPerfil: Int) {
        perfilUsuarioDao.borrarPerfil(idPerfil)
    }
}