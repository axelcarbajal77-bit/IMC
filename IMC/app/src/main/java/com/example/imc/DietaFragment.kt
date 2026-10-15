package com.example.imc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DietaFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 1. PRIMERO: Inflamos la vista y vinculamos los componentes visuales
        val root = inflater.inflate(R.layout.fragment_dieta, container, false)
        val tvMacros = root.findViewById<TextView>(R.id.tvMacrosDetalle)
        val tvMenu = root.findViewById<TextView>(R.id.tvMenuDetalle)

        // 2. SEGUNDO: Recuperamos los datos guardados en las SharedPreferences
        val prefs = requireActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE)
        val imc = prefs.getFloat("ultimo_imc", 0f)
        val genero = prefs.getString("genero_usuario", "Hombre") ?: "Hombre"
        val tdee = prefs.getFloat("tdee_usuario", 0f)

        // 3. TERCERO: Validación de seguridad por si no hay registros previos
        if (imc == 0f || tdee == 0f) {
            tvMacros.text = "N/A"
            tvMenu.text = "Por favor, calcula tus datos en la pestaña de la calculadora primero para generar tu plan."
            return root
        }

        var caloriasObjetivo = tdee
        var mensajeAlerta = ""

        // 4. CUARTO: Filtro de seguridad endocrina y ajuste calórico según objetivo
        when {
            imc < 18.5 -> {
                // Objetivo: Aumentar músculo (Superávit de +400 kcal)
                caloriasObjetivo = tdee + 400
            }
            imc >= 25.0 -> {
                // Objetivo: Bajar de peso (Déficit de -500 kcal)
                val caloriasCalculadas = tdee - 500

                // Implementación de Alerta de Seguridad Mínima
                val limiteSeguro = if (genero == "Hombre") 1500f else 1200f
                if (caloriasCalculadas < limiteSeguro) {
                    caloriasObjetivo = limiteSeguro
                    mensajeAlerta = "⚠️ ALERTA: El déficit original era críticamente bajo para tu tasa metabólica. Por protección endocrina, se ajustó al mínimo seguro de ${limiteSeguro.toInt()} kcal.\n\n"
                } else {
                    caloriasObjetivo = caloriasCalculadas
                }
            }
            else -> {
                // Objetivo: Mantenimiento
                caloriasObjetivo = tdee
            }
        }

        // 5. QUINTO: Contador Dinámico de Macros en Gramos
        val gramosProteina = (caloriasObjetivo * 0.30f) / 4f
        val gramosCarbohidratos = (caloriasObjetivo * 0.45f) / 4f
        val gramosGrasa = (caloriasObjetivo * 0.25f) / 9f

        // Guardamos el encabezado de los macros en una variable para usarla o concatenarla
        val encabezadoMacros = """
            $mensajeAlerta• Consumo Diario Recomendado: ${caloriasObjetivo.toInt()} kcal
            • Proteínas: ${gramosProteina.toInt()}g (30%)
            • Carbohidratos: ${gramosCarbohidratos.toInt()}g (45%)
            • Grasas Saludables: ${gramosGrasa.toInt()}g (25%)
            
        """.trimIndent()

        // 6. SEXTO: Estructura de control para asignar los menús detallados de comida
        when {
            imc < 18.5 -> {
                tvMacros.text = encabezadoMacros + "• Enfoque actual: Superávit Calórico"
                tvMenu.text = if (genero == "Hombre") {
                    "☀️ Desayuno:\n4 huevos revueltos, 2 rebanadas de pan integral, 1 licuado de plátano con avena entera y leche.\n\n🍏 Media Mañana:\nUn yogur griego entero con miel y un puñado de almendras.\n\n🍲 Almuerzo:\n250g de pechuga de pollo, 1.5 tazas de arroz blanco, 1/2 aguacate y vegetales.\n\n☕ Merienda:\nSándwich doble de crema de cacahuate.\n\n🌙 Cena:\n200g de filete de res magro, puré de papa y ensalada con aceite de oliva."
                } else {
                    "☀️ Desayuno:\n3 huevos revueltos con espinacas, 2 rebanadas de pan integral con aguacate y un licuado de fresa con avena.\n\n🍏 Media Mañana:\nYogur griego entero, fresas picadas y nueces.\n\n🍲 Almuerzo:\n180g de filete de salmón, 1 taza de quinoa cocida y espárragos asados.\n\n☕ Merienda:\nManzana con crema de almendras y tostadas de arroz.\n\n🌙 Cena:\n150g de pechuga de pollo asada, camote al horno y ensalada verde aderezada."
                }
            }
            imc < 24.9 -> {
                tvMacros.text = encabezadoMacros + "• Enfoque actual: Balance Normocalórico"
                tvMenu.text = "☀️ Desayuno:\nOmelet de 2 huevos con champiñones y pimientos, más 1 rebanada de pan tostado integral.\n\n🍏 Media Mañana:\nUna manzana o pera junto a una porción pequeña de queso cottage.\n\n🍲 Almuerzo:\n180g de pechuga de pollo o filete de pescado, 3/4 taza de arroz integral y ensalada fresca.\n\n☕ Merienda:\nUn puñado de nueces de la india o un batido ligero de proteína.\n\n🌙 Cena:\nTazón de verduras asadas con 120g de cubos de pollo a la plancha y un toque de ajonjolí."
            }
            imc < 29.9 -> {
                tvMacros.text = encabezadoMacros + "• Enfoque actual: Déficit Calórico Controlado"
                tvMenu.text = if (genero == "Hombre") {
                    "☀️ Desayuno:\nClaras de huevo (5 piezas) con jamón de pavo y jitomate, acompañado de té verde.\n\n🍏 Media Mañana:\nGelatina sin azúcar y 6 piezas de nueces.\n\n🍲 Almuerzo:\n220g de pescado blanco a la plancha, 1 taza de brócoli al vapor y 1/2 taza de quinoa.\n\n☕ Merienda:\nTiras de apio y zanahoria con 2 cucharadas de hummus.\n\n🌙 Cena:\nAtún en agua (1.5 latas) preparado con cebolla, tomate y limón, con 2 tostadas horneadas."
                } else {
                    "☀️ Desayuno:\nClaras de huevo (3 piezas) con 1 huevo entero, revueltas con ejotes picados.\n\n🍏 Media Mañana:\n1 taza de frutos rojos (fresas/arándanos) y 5 almendras.\n\n🍲 Almuerzo:\n150g de pechuga de pollo, abundante ensalada de hojas verdes y 1/2 papa cocida.\n\n☕ Merienda:\nYogur griego sin azúcar bajo en grasa con una pizca de canela.\n\n🌙 Cena:\nFilete de pescado al horno con calabacines y pimientos asados."
                }
            }
            else -> {
                tvMacros.text = encabezadoMacros + "• Enfoque actual: Restricción Calórica"
                tvMenu.text = if (genero == "Hombre") {
                    "⚠️ Plan enfocado en preservar masa muscular mientras se reduce tejido graso visceral:\n\n• Desayuno: Licuado verde con proteína en polvo aislada y 3 claras de huevo cocidas.\n• Almuerzo: 200g de pechuga de pollo deshebrada en caldo de de vegetales claro.\n• Cena: Salpicón de res magra con lechuga, rábano y pepino picado sin aderezos cremosos."
                } else {
                    "⚠️ Plan enfocado en resguardo metabólico y control de grasa subcutánea:\n\n• Desayuno: Omelet de 3 claras con espinacas y una taza de té verde sin endulzantes.\n• Almuerzo: 150g de filete de pescado blanco cocinado al vapor con una taza de espárragos.\n• Cena: Tazón de pechuga de pavo picada sobre una cama de lechuga orejona y rodajas de pepino."
                }
            }
        }

        return root
    }
}