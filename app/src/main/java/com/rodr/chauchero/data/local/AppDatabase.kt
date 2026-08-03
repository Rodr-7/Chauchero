package com.rodr.chauchero.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodr.chauchero.model.Gasto
import com.rodr.chauchero.model.PerfilUsuario

@Database(entities = [PerfilUsuario::class, Gasto::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gastoDao(): GastoDao
    abstract fun perfilUsuarioDao(): PerfilUsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chauchero_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}