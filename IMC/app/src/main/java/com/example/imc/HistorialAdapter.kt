package com.example.imc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private var lista: List<HistorialIMC>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.itemNombre)
        val tvFecha: TextView = view.findViewById(R.id.itemFecha)
        val tvImc: TextView = view.findViewById(R.id.itemImc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val registro = lista[position]
        holder.tvNombre.text = registro.nombre
        holder.tvFecha.text = registro.fecha
        holder.tvImc.text = "%.2f".format(registro.resultadoImc)

        // Asignación de color dinámico según el rango en la lista
        val imc = registro.resultadoImc
        val color = when {
            imc < 18.5 -> android.graphics.Color.parseColor("#1976D2") // Azul
            imc < 24.9 -> android.graphics.Color.parseColor("#388E3C") // Verde
            imc < 29.9 -> android.graphics.Color.parseColor("#F57C00") // Naranja
            else -> android.graphics.Color.parseColor("#D32F2F")       // Rojo
        }
        holder.tvImc.textColor = color
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<HistorialIMC>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}

// Extensión rápida para simplificar el manejo de color de texto en tiempo de ejecución
var TextView.textColor: Int
    get() = currentTextColor
    set(value) = setTextColor(value)