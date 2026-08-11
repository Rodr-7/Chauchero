package com.rodr.chauchero.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuario::class,
            parentColumns = ["id_perfil"],
            childColumns = ["id_perfil"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("id_perfil"), Index("id_categoria")]
)
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_gasto")
    val idGasto: Int = 0,

    @ColumnInfo(name = "id_perfil")
    val idPerfil: Int,

    @ColumnInfo(name = "nombre_gasto")
    val nombreGasto: String,

    @ColumnInfo(name = "id_categoria")
    val idCategoria: Int,

    @ColumnInfo(name = "prioridad")
    val prioridad: Prioridad,

    @ColumnInfo(name = "valor")
    val valor: Int,

    @ColumnInfo(name = "estado_pagado")
    val estadoPagado: Boolean = false
) {
    /**
     * Puente temporal para consumidores de presentación todavía basados en texto.
     * No se persiste: la fuente de verdad es [idCategoria].
     */
    @get:Ignore
    val categoria: String
        get() = idCategoria.toString()

    @Ignore
    constructor(
        idGasto: Int = 0,
        idPerfil: Int,
        nombreGasto: String,
        categoria: String,
        prioridad: Prioridad,
        valor: Int,
        estadoPagado: Boolean = false
    ) : this(
        idGasto = idGasto,
        idPerfil = idPerfil,
        nombreGasto = nombreGasto,
        idCategoria = categoria.toIntOrNull() ?: 0,
        prioridad = prioridad,
        valor = valor,
        estadoPagado = estadoPagado
    )
}
