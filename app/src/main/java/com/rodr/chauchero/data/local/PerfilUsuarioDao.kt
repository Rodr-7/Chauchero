package com.rodr.chauchero.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodr.chauchero.model.PerfilUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilUsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: PerfilUsuario)

    @Query("SELECT * FROM perfil_usuario WHERE id_perfil = :idPerfil")
    fun mostrarPerfil(idPerfil: Int): Flow<PerfilUsuario?>

    @Update
    suspend fun modificarPerfil(perfil: PerfilUsuario)

    @Query("DELETE FROM perfil_usuario WHERE id_perfil = :idPerfil")
    suspend fun borrarPerfil(idPerfil: Int)
}