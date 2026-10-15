package com.example.imc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // 🌟 Añadido para el botón
import android.widget.TextView
import android.widget.Toast // 🌟 Añadido para el aviso en pantalla
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialFragment : Fragment() {

    private lateinit var tvHistorialContenido: TextView
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño de tu historial
        val root = inflater.inflate(R.layout.fragment_historial, container, false)

        // 1. Vincula el TextView donde pintas los datos
        tvHistorialContenido = root.findViewById<TextView>(R.id.tvHistorialContenido)

        // 2. Inicializa la misma base de datos
        database = AppDatabase.getDatabase(requireContext())

        // 🌟 3. NUEVO: Lógica para el botón Limpiar
        val btnBorrarTodo = root.findViewById<Button>(R.id.btnBorrarTodo)
        btnBorrarTodo.setOnClickListener {
            // Ejecutamos una corrutina en segundo plano para no congelar la app
            lifecycleScope.launch(Dispatchers.IO) {
                // Borra la tabla físicamente de SQLite a través de Room
                database.historialDao().borrarTodo()

                // Volvemos al hilo de la interfaz gráfica para actualizar el texto
                withContext(Dispatchers.Main) {
                    tvHistorialContenido.text = "HISTORIAL DE CÁLCULOS:\n\nNo hay registros guardados aún."
                    Toast.makeText(requireContext(), "Historial eliminado con éxito", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }

    // onResume se ejecuta CADA VEZ que el usuario abre esta pantalla
    override fun onResume() {
        super.onResume()
        cargarDatosDesdeRoom()
    }

    private fun cargarDatosDesdeRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            val listaRegistros = database.historialDao().obtenerTodo()
            var textoAcumulado = "HISTORIAL DE CÁLCULOS:\n\n"

            if (listaRegistros.isEmpty()) {
                textoAcumulado += "No hay registros guardados aún."
            } else {
                listaRegistros.reversed().forEach { registro ->
                    textoAcumulado += "• ${registro.nombre}\n  IMC: %.2f | Fecha: ${registro.fecha}\n\n"
                        .format(registro.resultadoImc)
                }
            }

            withContext(Dispatchers.Main) {
                if (isAdded && isResumed) {
                    tvHistorialContenido.text = textoAcumulado
                }
            }
        }
    }
}