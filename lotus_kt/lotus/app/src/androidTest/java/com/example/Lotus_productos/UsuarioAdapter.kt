package com.example.Lotus_productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuarioAdapter(
    private var listaUsuarios: List<Usuario>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    // Interfaz para manejar clics en editar o eliminar
    interface OnItemClickListener {
        fun onEdit(usuario: Usuario)
        fun onDelete(usuario: Usuario)
    }

    // ViewHolder: contiene las vistas de cada item
    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val tvNombreCompleto: TextView = itemView.findViewById(R.id.tvNombreCompleto)
        val tvTipoNumero: TextView = itemView.findViewById(R.id.tvTipoNumero)
        val tvEdadCard: TextView = itemView.findViewById(R.id.tvEdadCard)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        val tvGenero: TextView = itemView.findViewById(R.id.tvGenero)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        // Asignar los valores a los TextView
        holder.tvId.text = "ID: ${usuario.id}"
        holder.tvNombreCompleto.text = "${usuario.nombre} ${usuario.apellidos}"
        holder.tvTipoNumero.text = "${usuario.tipoId}: ${usuario.numeroId}"
        holder.tvEdadCard.text = "Fecha nac.: ${usuario.fechaNacimiento}"
        holder.tvTelefono.text = "Tel: ${usuario.telefono}"
        holder.tvDireccion.text = "Dirección: ${usuario.direccion}"
        holder.tvGenero.text = "Género: ${usuario.genero}"

        // Eventos
        holder.btnEditar.setOnClickListener { listener.onEdit(usuario) }
        holder.btnEliminar.setOnClickListener { listener.onDelete(usuario) }
    }

    override fun getItemCount(): Int = listaUsuarios.size

    // Permite actualizar la lista cuando cambian los datos
    fun actualizarLista(nuevaLista: List<Usuario>) {
        listaUsuarios = nuevaLista
        notifyDataSetChanged()
    }
}