package com.example.Lotus_productos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper_(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LotusDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USUARIOS = "Usuarios"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                tipo_id TEXT NOT NULL,
                numero_id TEXT NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                edad INTEGER NOT NULL,
                telefono TEXT,
                correo TEXT,
                direccion TEXT,
                genero TEXT
            )
        """.trimIndent()
        db.execSQL(createUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    // ---------------- TABLA USUARIOS ----------------

    fun insertarUsuario(u: Usuario): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nombre", u.nombre)
            put("apellidos", u.apellidos)
            put("tipo_id", u.tipoId)
            put("numero_id", u.numeroId)
            put("fecha_nacimiento", u.fechaNacimiento)
            put("edad", u.edad)
            put("telefono", u.telefono)
            put("correo", u.correo)
            put("direccion", u.direccion)
            put("genero", u.genero)
        }
        val id = db.insert(TABLE_USUARIOS, null, cv)
        db.close()
        return id
    }

    fun listarUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIOS ORDER BY id DESC", null)

        val colId = cursor.getColumnIndexOrThrow("id")
        val colNombre = cursor.getColumnIndexOrThrow("nombre")
        val colApellidos = cursor.getColumnIndexOrThrow("apellidos")
        val colTipoId = cursor.getColumnIndexOrThrow("tipo_id")
        val colNumeroId = cursor.getColumnIndexOrThrow("numero_id")
        val colFechaNac = cursor.getColumnIndexOrThrow("fecha_nacimiento")
        val colEdad = cursor.getColumnIndexOrThrow("edad")
        val colTelefono = cursor.getColumnIndexOrThrow("telefono")
        val colCorreo = cursor.getColumnIndexOrThrow("correo")
        val colDireccion = cursor.getColumnIndexOrThrow("direccion")
        val colGenero = cursor.getColumnIndexOrThrow("genero")

        if (cursor.moveToFirst()) {
            do {
                val usuario = Usuario(
                    id = cursor.getInt(colId),
                    nombre = cursor.getString(colNombre),
                    apellidos = cursor.getString(colApellidos),
                    tipoId = cursor.getString(colTipoId),
                    numeroId = cursor.getString(colNumeroId),
                    fechaNacimiento = cursor.getString(colFechaNac),
                    edad = cursor.getInt(colEdad),
                    telefono = cursor.getString(colTelefono) ?: "",
                    correo = cursor.getString(colCorreo) ?: "",
                    direccion = cursor.getString(colDireccion) ?: "",
                    genero = cursor.getString(colGenero) ?: ""
                )
                lista.add(usuario)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun actualizarUsuario(u: Usuario): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nombre", u.nombre)
            put("apellidos", u.apellidos)
            put("tipo_id", u.tipoId)
            put("numero_id", u.numeroId)
            put("fecha_nacimiento", u.fechaNacimiento)
            put("edad", u.edad)
            put("telefono", u.telefono)
            put("correo", u.correo)
            put("direccion", u.direccion)
            put("genero", u.genero)
        }

        val filasAfectadas = db.update(
            TABLE_USUARIOS,
            cv,
            "id = ?",
            arrayOf(u.id.toString())
        )
        db.close()
        return filasAfectadas
    }

    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        val filasEliminadas = db.delete(TABLE_USUARIOS, "id = ?", arrayOf(id.toString()))
        db.close()
        return filasEliminadas
    }
}
