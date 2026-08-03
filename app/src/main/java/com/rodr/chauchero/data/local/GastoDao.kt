package com.rodr.chauchero.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rodr.chauchero.model.Gasto
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGasto(gasto: Gasto)

    @Query("SELECT * FROM gastos WHERE id_gasto = :idGasto")
    fun mostrarGasto(idGasto: Int): Flow<Gasto?>

    @Query("SELECT * FROM gastos")
    fun mostrarTodosLosGastos(): Flow<List<Gasto>>

    @Update
    suspend fun modificarGasto(gasto: Gasto)

    @Query("DELETE FROM gastos WHERE id_gasto = :idGasto")
    suspend fun borrarGasto(idGasto: Int)
}