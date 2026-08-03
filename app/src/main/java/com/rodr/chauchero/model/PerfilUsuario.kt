package com.rodr.chauchero.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_usuario")
data class PerfilUsuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_perfil")
    val idPerfil: Int = 0,

    @ColumnInfo(name = "nombre_perfil")
    val nombrePerfil: String,

    @ColumnInfo(name = "salario_fijo")
    val salarioFijo: Int,

    @ColumnInfo(name = "saldo_actual")
    val saldoActual: Int
)