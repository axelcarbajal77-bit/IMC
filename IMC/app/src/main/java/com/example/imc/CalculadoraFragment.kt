package com.example.imc

import com.example.imc.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CalculadoraFragment : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etPeso: EditText
    private lateinit var etAltura: EditText
    private lateinit var etEdad: EditText
    private lateinit var rgGenero: RadioGroup
    private lateinit var spinnerActividad: Spinner
    private lateinit var btnCalcular: Button
    private lateinit var tvResultadoIMC: TextView
    private lateinit var tvEstadoIMC: TextView

    private lateinit var database: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calculadora, container, false)

        // Inicializar componentes
        etNombre = root.findViewById(R.id.etNombre)
        etPeso = root.findViewById(R.id.etPeso)
        etAltura = root.findViewById(R.id.etAltura)
        etEdad = root.findViewById(R.id.etEdad)
        rgGenero = root.findViewById(R.id.rgGenero)
        spinnerActividad = root.findViewById(R.id.spinnerActividad)
        btnCalcular = root.findViewById(R.id.btnCalcular)
        tvResultadoIMC = root.findViewById(R.id.tvResultadoIMC)
        tvEstadoIMC = root.findViewById(R.id.tvEstadoIMC)

        database = AppDatabase.getDatabase(requireContext())

        // Configurar Spinner de actividad
        val opcionesActividad = arrayOf(
            "Sedentario (Poco o ningún ejercicio)",
            "Ligero (Ejercicio 1-3 días/semana)",
            "Moderado (Ejercicio 3-5 días/semana)",
            "Intenso (Ejercicio 6-7 días/semana)"
        )
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesActividad)
        spinnerActividad.adapter = spinnerAdapter

        btnCalcular.setOnClickListener {
            val nombre = etNombre.text.toString()
            val pesoStr = etPeso.text.toString().replace(",", ".")
            val alturaStr = etAltura.text.toString().replace(",", ".")
            val edadStr = etEdad.text.toString()

            val genero = if (rgGenero.checkedRadioButtonId == R.id.rbHombre) "Hombre" else "Mujer"
            val posicionActividad = spinnerActividad.selectedItemPosition

            val peso = pesoStr.toFloatOrNull()
            val altura = alturaStr.toFloatOrNull()
            val edad = edadStr.toIntOrNull()

            if (nombre.isNotEmpty() && peso != null && altura != null && altura > 0 && edad != null) {
                val imc = peso / (altura * altura)

                // Metodología Mifflin-St Jeor para TDEE
                val alturaCm = altura * 100
                val bmr = if (genero == "Hombre") {
                    (10 * peso) + (6.25f * alturaCm) - (5 * edad) + 5
                } else {
                    (10 * peso) + (6.25f * alturaCm) - (5 * edad) - 161
                }

                val factorActividad = when (posicionActividad) {
                    0 -> 1.2f
                    1 -> 1.375f
                    2 -> 1.55f
                    3 -> 1.725f
                    else -> 1.2f
                }
                val tdee = bmr * factorActividad

                // Guardado rápido en SharedPreferences para uso en DietaFragment
                requireActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE)
                    .edit()
                    .putFloat("ultimo_imc", imc)
                    .putString("genero_usuario", genero)
                    .putFloat("tdee_usuario", tdee)
                    .apply()

                // Llamar a tu función de rangos
                val (estado, recomendacion, colorRes) = obtenerRecomendacion(imc)

                // Imprimir en la interfaz de usuario
                tvResultadoIMC.text = String.format("%.2f", imc)
                tvEstadoIMC.text = "$estado\n$recomendacion"
                tvEstadoIMC.setTextColor(resources.getColor(colorRes, null))

                // FORMATO DE FECHA TEXTO: Convertimos la fecha actual a String según tu modelo HistorialIMC.kt
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                // INSTANCIACIÓN CORREGIDA: Atributos exactos de tu entidad Room
                val historialRegistro = HistorialIMC(
                    nombre = nombre,
                    resultadoImc = imc, // Ajustado de 'imc' a 'resultadoImc'
                    fecha = formatoFecha // Ajustado de 'Long' a 'String'
                )

                // Almacenamiento asíncrono en Room
                lifecycleScope.launch(Dispatchers.IO) {
                    database.historialDao().insertarRegistro(historialRegistro)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Cálculo guardado con éxito", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(requireContext(), "Por favor, llena todos los campos correctamente.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun obtenerRecomendacion(imc: Float): Triple<String, String, Int> {
        return when {
            imc < 18.5 -> Triple("Bajo peso", "Considera aumentar tu ingesta calórica.", android.R.color.holo_blue_dark)
            imc < 24.9 -> Triple("Peso Normal", "¡Excelente! Mantén tus hábitos actuales.", android.R.color.holo_green_dark)
            imc < 29.9 -> Triple("Sobrepeso", "Intenta aumentar tu actividad física diaria.", android.R.color.holo_orange_dark)
            else -> Triple("Obesidad", "Te recomendamos consultar con un especialista.", android.R.color.holo_red_dark)
        }
    }
}