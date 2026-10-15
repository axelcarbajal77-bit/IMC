package com.example.imc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class EjercicioFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_ejercicio, container, false)

        val tvEnfoque = root.findViewById<TextView>(R.id.tvEnfoqueDetalle)
        val tvRutina = root.findViewById<TextView>(R.id.tvRutinaDetalle)

        val prefs = requireActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE)
        val imc = prefs.getFloat("ultimo_imc", 0f)
        val genero = prefs.getString("genero_usuario", "Hombre") ?: "Hombre"

        if (imc == 0f) {
            tvEnfoque.text = "N/A"
            tvRutina.text = "Por favor, calcula tu IMC primero en la sección de la calculadora para desplegar los entrenamientos."
            return root
        }

        when {
            imc < 18.5 -> {
                if (genero == "Hombre") {
                    tvEnfoque.text = "• Frecuencia: 3 días/semana (L-M-V)\n• Objetivo: Hipertrofia miofibrilar pura"
                    tvRutina.text = "🏋️ Rutina de Fuerza Pesada:\n\n• Sentadillas libres con barra: 4 series x 8 reps.\n• Press de banca plano: 4 series x 8 reps.\n• Peso muerto rumano: 3 series x 10 reps.\n• Remo con barra para espalda: 3 series x 10 reps.\n\n⚠️ Evitar sesiones de cardio extensas para prevenir el catabolismo muscular."
                } else {
                    tvEnfoque.text = "• Frecuencia: 3 días/semana (L-M-V)\n• Objetivo: Desarrollo muscular y densidad ósea"
                    tvRutina.text = "🏋️ Rutina de Estímulo Localizado:\n\n• Prensa de piernas: 4 series x 10 reps.\n• Desplantes estáticos con mancuernas: 3 series x 12 reps por pierna.\n• Press superior con mancuernas: 3 series x 10 reps.\n• Jalón al pecho en polea: 3 series x 12 reps.\n\n⚠️ Mantener descansos completos de 90 segundos entre series."
                }
            }
            imc < 24.9 -> {
                tvEnfoque.text = "• Frecuencia: 4 a 5 días/semana\n• Objetivo: Acondicionamiento físico general e híbrido"
                tvRutina.text = if (genero == "Hombre") {
                    "💪 Distribución Torso/Pierna:\n\n• Lunes y Jueves: Tren Superior (Press militar, dominadas, lagartijas).\n• Martes y Viernes: Tren Inferior (Sentadilla goblet, curl femoral, pantorrillas).\n• Cardio: 20 minutos de carrera moderada post-pesas."
                } else {
                    "⏳ Distribución Estética Funcional:\n\n• Lunes y Jueves: Tren Inferior enfocado en glúteo e isquiotibiales.\n• Martes y Viernes: Tren Superior y zona core (Abdomen, hombros, espalda).\n• Cardio: 35 minutos de elíptica o caminata inclinada a ritmo constante."
                }
            }
            imc < 29.9 -> {
                if (genero == "Hombre") {
                    tvEnfoque.text = "• Frecuencia: 4 días/semana\n• Objetivo: Gasto calórico mediante circuitos de alta densidad"
                    tvRutina.text = "🔥 Circuito de Alta Intensidad (HIIT):\n\nRealizar 4 vueltas deteniendo el reloj lo menos posible:\n• 1. Flexiones de pecho: 15 repeticiones.\n• 2. Sentadillas con salto: 20 repeticiones.\n• 3. Remo con mancuerna: 12 repeticiones por lado.\n• 4. Burpees: 10 repeticiones.\n\n🚴 Cardio: 15 minutos en bicicleta estática alternando 1 min rápido / 1 min lento."
                } else {
                    tvEnfoque.text = "• Frecuencia: 4 días/semana\n• Objetivo: Oxidación de grasas protegiendo las articulaciones"
                    tvRutina.text = "🔥 Circuito Metabólico Funcional:\n\nRealizar 4 vueltas continuas:\n• 1. Desplantes invertidos (sin impacto de rodilla): 12 por pierna.\n• 2. Press de hombros ligero: 15 repeticiones.\n• 3. Elevación de cadera (Glute bridge) con peso: 20 repeticiones.\n• 4. Plancha abdominal: Sostener 45 segundos.\n\n🚴 Cardio: 30 minutos de caminadora con inclinación (sin correr)."
                }
            }
            else -> {
                tvEnfoque.text = "• Frecuencia: 3 días/semana\n• Objetivo: Cuidado articular extremo y gasto metabólico bajo impacto"
                tvRutina.text = if (genero == "Hombre") {
                    "🏊 Entrenamiento de Resistencia Segura:\n\n• Prensa horizontal de piernas (evita sobrecarga lumbar): 3 series x 15 reps.\n• Remo sentado en polea: 3 series x 15 reps.\n• Press de pecho asistido en máquina: 3 series x 15 reps.\n\n🚴 Cardio Obligatorio: 40 minutos en bicicleta fija con resistencia media. Cero saltos."
                } else {
                    "🏊 Entrenamiento Aeróbico y de Tonificación Suave:\n\n• Extensión de piernas en máquina: 3 series x 15 reps.\n• Jalón al pecho con agarre abierto: 3 series x 15 reps.\n• Puentes de glúteo en tapete (peso corporal): 4 series x 20 reps.\n\n🚴 Cardio Obligatorio: 45 minutos de natación de estilo libre o elíptica suave."
                }
            }
        }

        return root
    }
}