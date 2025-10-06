package com.example.Lotus_productos

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val tipoId: String,
    val numeroId: String,
    val correo: String,
    val fechaNacimiento: String,
    val edad: Int,
    val telefono: String,
    val direccion: String,
    val genero: String
)