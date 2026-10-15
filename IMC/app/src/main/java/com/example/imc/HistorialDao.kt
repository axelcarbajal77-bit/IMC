package com.example.imc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistorialDao {

    // 1. Obtener los registros ordenados del más nuevo al más viejo
    @Query("SELECT * FROM HistorialIMC ORDER BY id DESC")
    suspend fun obtenerTodo(): List<HistorialIMC>

    // 2. Insertar un nuevo registro de IMC
    @Insert
    suspend fun insertarRegistro(registro: HistorialIMC)

    // 3. Limpiar la tabla por completo
    @Query("DELETE FROM HistorialIMC")
    suspend fun borrarTodo()
}