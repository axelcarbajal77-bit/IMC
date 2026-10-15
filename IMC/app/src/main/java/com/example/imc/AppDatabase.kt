package com.example.imc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistorialIMC::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historialDao(): HistorialDao

    // El companion object funciona como los métodos estáticos en Java.
    // Aquí creamos la función que le hace falta a tu CalculadoraFragment.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos; si no, creamos la base de datos
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "imc_database" // Nombre del archivo interno de la BD
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}