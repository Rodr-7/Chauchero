package com.rodr.chauchero.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rodr.chauchero.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre COLLATE NOCASE ASC")
    fun mostrarTodasLasCategorias(): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarCategoria(categoria: Categoria): Long
}
