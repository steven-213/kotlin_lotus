package com.example.Lotus_productos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CompraActivity : AppCompatActivity() {

    private lateinit var listaProductos: MutableList<Producto>
    private lateinit var adapter: ProductoAdapter
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIvaTotal: TextView
    private lateinit var tvTotalFinal: TextView
    private lateinit var tvUsuarioCompra: TextView
    private lateinit var btnComprarTodo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIvaTotal = findViewById(R.id.tvIvaTotal)
        tvTotalFinal = findViewById(R.id.tvTotalFinal)
        tvUsuarioCompra = findViewById(R.id.tvUsuarioCompra)
        btnComprarTodo = findViewById(R.id.btnComprarTodo)

        val nombreUsuario = intent.getStringExtra("usuarioNombre") ?: "Usuario"
        tvUsuarioCompra.text = "Comprando como: $nombreUsuario"

        listaProductos = mutableListOf(
            Producto(1, "Aceite de masaje relajante 100ml", 120.0),
            Producto(2, "Crema hidratante corporal 200ml", 90.0),
            Producto(3, "Exfoliante facial suave 50g", 70.0),
            Producto(4, "Mascarilla de arcilla 100g", 80.0),
            Producto(5, "Jabón artesanal de lavanda 100g", 35.0),
            Producto(6, "Gel de baño aromático 250ml", 60.0),
            Producto(7, "Velas aromáticas de soja 120g", 50.0),
            Producto(8, "Loción tonificante para piel seca 200ml", 100.0),
            Producto(9, "Sérum facial rejuvenecedor 30ml", 150.0),
            Producto(10, "Esponja natural para baño", 25.0)
        )

        adapter = ProductoAdapter(listaProductos) { actualizarTotales() }

        findViewById<RecyclerView>(R.id.rvProductos).apply {
            layoutManager = LinearLayoutManager(this@CompraActivity)
            adapter = this@CompraActivity.adapter
        }

        btnComprarTodo.setOnClickListener {
            // Ir a pantalla de gracias
            startActivity(Intent(this, GraciasActivity::class.java))
            listaProductos.forEach { it.cantidad = 0 }
            adapter.notifyDataSetChanged()
            actualizarTotales()
        }

        actualizarTotales()
    }

    private fun actualizarTotales() {
        val subtotal = listaProductos.sumOf { it.precio * it.cantidad }
        val iva = subtotal * 0.19
        val total = subtotal + iva

        tvSubtotal.text = "Subtotal: $${String.format("%.2f", subtotal)}"
        tvIvaTotal.text = "IVA: $${String.format("%.2f", iva)}"
        tvTotalFinal.text = "Total: $${String.format("%.2f", total)}"
    }
}
