package com.rodr.chauchero.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_categoria")
    val idCategoria: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String
)
