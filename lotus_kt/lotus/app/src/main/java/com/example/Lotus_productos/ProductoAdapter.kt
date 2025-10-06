package com.example.Lotus_productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductoAdapter(
    private val listaProductos: List<Producto>,
    private val onCantidadChanged: () -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        val btnMas: Button = view.findViewById(R.id.btnMas)
        val btnMenos: Button = view.findViewById(R.id.btnMenos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "$${producto.precio}"
        holder.tvCantidad.text = producto.cantidad.toString()

        holder.btnMas.setOnClickListener {
            producto.cantidad++
            holder.tvCantidad.text = producto.cantidad.toString()
            onCantidadChanged()
        }

        holder.btnMenos.setOnClickListener {
            if (producto.cantidad > 0) {
                producto.cantidad--
                holder.tvCantidad.text = producto.cantidad.toString()
                onCantidadChanged()
            }
        }
    }

    override fun getItemCount() = listaProductos.size
}
