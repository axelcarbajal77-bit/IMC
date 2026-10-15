package com.example.imc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HistorialIMC")
data class HistorialIMC(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,          // Debe ser nombre
    val resultadoImc: Float,     // Debe ser resultadoImc (Float)
    val fecha: String            // Debe ser fecha (String)
)