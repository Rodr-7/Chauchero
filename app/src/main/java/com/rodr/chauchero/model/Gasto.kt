package com.rodr.chauchero.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuario::class,
            parentColumns = ["id_perfil"],
            childColumns = ["id_perfil"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    val id_gasto: Int = 0,
    val id_perfil: Int,
    val nombre_gasto: String,
    val categoria: String,
    val prioridad: Prioridad,
    val valor: Int,
    val estado_pagado: Boolean = false // false = pendiente, true = pagado
)