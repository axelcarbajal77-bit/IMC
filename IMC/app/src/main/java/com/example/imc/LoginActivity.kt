package com.example.imc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el motor de autenticación de Firebase
        auth = FirebaseAuth.getInstance()

        // 👁️ PERSISTENCIA: Si el usuario ya se había logueado antes, pasa directo sin pedir contraseña otra vez
        if (auth.currentUser != null) {
            redirigirAlMain()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // ACCIÓN: INICIAR SESIÓN
        btnLogin.setOnClickListener {
            val correo = etEmail.text.toString().trim()
            val contra = etPassword.text.toString().trim()

            if (correo.isNotEmpty() && contra.isNotEmpty()) {
                auth.signInWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(this) { tarea ->
                        if (tarea.isSuccessful) {
                            Toast.makeText(this, "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show()
                            redirigirAlMain()
                        } else {
                            Toast.makeText(this, "Error de acceso: ${tarea.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor completa los dos campos", Toast.LENGTH_SHORT).show()
            }
        }

        // ACCIÓN: REGISTRAR UN NUEVO CORREO EN LA BASE DE DATOS
        btnRegister.setOnClickListener {
            val correo = etEmail.text.toString().trim()
            val contra = etPassword.text.toString().trim()

            if (correo.isNotEmpty() && contra.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(this) { tarea ->
                        if (tarea.isSuccessful) {
                            Toast.makeText(this, "Cuenta registrada exitosamente", Toast.LENGTH_SHORT).show()
                            redirigirAlMain()
                        } else {
                            Toast.makeText(this, "Error al registrar: ${tarea.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Ingresa un correo y contraseña para crear tu cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redirigirAlMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Termina la actividad para que si presionan el botón físico de atrás no regrese al Login
    }
}