package com.example.Lotus_productos

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    var cantidad: Int = 0
)
