package com.example.Lotus_productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuarioAdapter(
    private val lista: List<Usuario>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(usuario: Usuario)
        fun onDelete(usuario: Usuario)
        fun onComprar(usuario: Usuario)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvDetallesUsuario: TextView = view.findViewById(R.id.tvDetallesUsuario)
        val btnEditarUsuario: ImageButton = view.findViewById(R.id.btnEditarUsuario)
        val btnEliminarUsuario: ImageButton = view.findViewById(R.id.btnEliminarUsuario)
        val btnComprar: Button = view.findViewById(R.id.btnComprar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = lista[position]

        holder.tvNombreUsuario.text = "${u.nombre} ${u.apellidos}"

        // Concatenar detalles en un solo TextView
        holder.tvDetallesUsuario.text = "Correo: ${u.correo}\nTel: ${u.telefono}\nDir: ${u.direccion}"

        holder.btnEditarUsuario.setOnClickListener { listener.onEdit(u) }
        holder.btnEliminarUsuario.setOnClickListener { listener.onDelete(u) }
        holder.btnComprar.setOnClickListener { listener.onComprar(u) }
    }

    override fun getItemCount(): Int = lista.size
}

