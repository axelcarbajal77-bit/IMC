package com.example.imc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // Ocultamos la barra superior para una experiencia inmersiva limpia
        supportActionBar?.hide()

        // Hilo de ciclo de vida seguro para pausar 3 segundos antes del intent
        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@BienvenidaActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Destruye esta actividad para que el usuario no regrese al presionar 'atrás'
        }
    }
}