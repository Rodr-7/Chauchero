package com.rodr.chauchero.model

import androidx.room.ColumnInfo
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
    @ColumnInfo(name = "id_gasto")
    val idGasto: Int = 0,

    @ColumnInfo(name = "id_perfil")
    val idPerfil: Int,

    @ColumnInfo(name = "nombre_gasto")
    val nombreGasto: String,

    @ColumnInfo(name = "categoria")
    val categoria: String,

    @ColumnInfo(name = "prioridad")
    val prioridad: Prioridad,

    @ColumnInfo(name = "valor")
    val valor: Int,

    @ColumnInfo(name = "estado_pagado")
    val estadoPagado: Boolean = false
)