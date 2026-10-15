package com.example.imc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        val tvUsuarioCorreo = view.findViewById<TextView>(R.id.tvUsuarioCorreo)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)

        // 👁️ Mostrar el correo del usuario que está firmado actualmente
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            tvUsuarioCorreo.text = usuarioActual.email
        }

        // ACCIÓN: CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener {
            auth.signOut() // 🌟 Google Firebase borra el token de sesión del teléfono

            Toast.makeText(activity, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

            // Redirigir al usuario de vuelta al LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            // Estas banderas limpian el historial de pantallas para que no pueda regresar con el botón físico
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}