package com.example.imc

import com.example.imc.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Inicializamos la base de datos de forma segura (Lazy)
    private val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "base-imc").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- CONFIGURACIÓN DEL MENÚ DE HAMBURGUESA ---
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // CONTROLADOR ÚNICO DEL MENÚ LATERAL (Corregido y unificado)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.nav_calculadora -> CalculadoraFragment()

                // Si tienes fragmentos dedicados para ejercicio y dieta, se abren aquí:
                R.id.nav_ejercicio -> EjercicioFragment()
                R.id.nav_dieta -> DietaFragment()

                R.id.nav_historial -> HistorialFragment()

                // 🌟 ¡AQUÍ QUEDÓ AGREGADO EL PASO 2 QUE ME PEDISTE!
                R.id.nav_ayuda -> AyudaFragment()

                // 🚀 ¡PEGA ESTA NUEVA LÍNEA JUSTO AQUÍ ABAJO!
                R.id.nav_perfil -> PerfilFragment()

                else -> CalculadoraFragment()
            }

            // Realiza la transacción para pintar el fragmento en pantalla
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            drawerLayout.closeDrawers()
            true
        }

        // --- PANTALLA INICIAL POR DEFECTO ---
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalculadoraFragment())
                .commit()
            navigationView.setCheckedItem(R.id.nav_calculadora)
        }
    } // Clierre del onCreate


    // Función para leer la base de datos y mostrarla en el TextView
    private fun mostrarHistorial(tv: TextView) {
        lifecycleScope.launch {
            val lista = db.historialDao().obtenerTodo()
            var textoAcumulado = "HISTORIAL RECIENTE:\n"

            lista.take(5).forEach { registro ->
                textoAcumulado += "• ${registro.nombre}: %.2f (${registro.fecha})\n".format(registro.resultadoImc)
            }
            tv.text = textoAcumulado
        }
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