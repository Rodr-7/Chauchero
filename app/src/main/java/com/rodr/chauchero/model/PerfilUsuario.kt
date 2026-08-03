package com.rodr.chauchero.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_usuario")
data class PerfilUsuario(
    @PrimaryKey(autoGenerate = true)
    val id_perfil: Int = 0,
    val nombre_perfil: String,
    val salario_fijo: Int = 0,
    val saldo_actual: Int = 0
)