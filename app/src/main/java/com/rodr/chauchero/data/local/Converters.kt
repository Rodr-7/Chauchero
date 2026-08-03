package com.rodr.chauchero.data.local

import androidx.room.TypeConverter
import com.rodr.chauchero.model.Prioridad

class Converters {
    @TypeConverter
    fun fromPrioridad(prioridad: Prioridad): String {
        return prioridad.name
    }

    @TypeConverter
    fun toPrioridad(value: String): Prioridad {
        return Prioridad.valueOf(value)
    }
}