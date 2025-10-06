package com.example.Lotus_productos

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper_
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var spTipoId: Spinner
    private lateinit var etNumeroId: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etEdad: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etDireccion: EditText
    private lateinit var spGenero: Spinner
    private lateinit var btnAgregarUsuario: Button
    private lateinit var rvUsuarios: RecyclerView

    private val listaUsuarios = mutableListOf<Usuario>()
    private lateinit var adapter: UsuarioAdapter
    private var seleccionadoId: Int? = null
    private lateinit var tipos: Array<String>
    private lateinit var generos: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        dbHelper = DbHelper_(this)

        // --- Vistas ---
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        spTipoId = findViewById(R.id.spTipoId)
        etNumeroId = findViewById(R.id.etNumeroId)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etEdad = findViewById(R.id.etEdad)
        etTelefono = findViewById(R.id.etTelefono)
        etCorreo = findViewById(R.id.etCorreo)
        etDireccion = findViewById(R.id.etDireccion)
        spGenero = findViewById(R.id.spGenero)
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario)
        rvUsuarios = findViewById(R.id.rvUsuarios)

        // Spinner tipo de identificación
        tipos = arrayOf("CC", "TI", "CE", "RC")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoId.adapter = adapterSpinner

        // Spinner género
        generos = arrayOf("Masculino", "Femenino", "Otro")
        val adapterGenero = ArrayAdapter(this, android.R.layout.simple_spinner_item, generos)
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGenero.adapter = adapterGenero

        // RecyclerView y Adapter
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        adapter = UsuarioAdapter(listaUsuarios, object : UsuarioAdapter.OnItemClickListener {
            override fun onEdit(usuario: Usuario) {
                llenarFormularioParaEdicion(usuario)
            }

            override fun onDelete(usuario: Usuario) {
                confirmarYEliminar(usuario.id)
            }

            override fun onComprar(usuario: Usuario) {
                val intent = Intent(this@MainActivity, CompraActivity::class.java)
                intent.putExtra("usuarioNombre", "${usuario.nombre} ${usuario.apellidos}")
                startActivity(intent)
            }
        })
        rvUsuarios.adapter = adapter

        // DatePicker
        etFechaNacimiento.setOnClickListener { mostrarDatePicker() }

        // Botón agregar/actualizar
        btnAgregarUsuario.setOnClickListener {
            val usuario = leerFormulario(seleccionadoId) ?: return@setOnClickListener
            if (seleccionadoId == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val id = dbHelper.insertarUsuario(usuario)
                    withContext(Dispatchers.Main) {
                        if (id != -1L) {
                            Toast.makeText(this@MainActivity, "Usuario registrado", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(this@MainActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val filas = dbHelper.actualizarUsuario(usuario)
                    withContext(Dispatchers.Main) {
                        if (filas > 0) {
                            Toast.makeText(this@MainActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(this@MainActivity, "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Cargar usuarios al inicio
        cargarUsuarios()
    }

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, y, m, d ->
            val fechaIso = String.format("%04d-%02d-%02d", y, m + 1, d)
            etFechaNacimiento.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            etFechaNacimiento.tag = fechaIso
            etEdad.setText(calcularEdad(y, m, d).toString())
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun calcularEdad(year: Int, month: Int, day: Int): Int {
        val hoy = Calendar.getInstance()
        val nac = Calendar.getInstance().apply { set(year, month, day) }
        var edad = hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nac.get(Calendar.DAY_OF_YEAR)) edad--
        return edad
    }

    private fun leerFormulario(idOpt: Int? = null): Usuario? {
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val tipoId = spTipoId.selectedItem.toString()
        val numeroId = etNumeroId.text.toString().trim()
        val fecha = etFechaNacimiento.tag as? String ?: ""
        val edad = etEdad.text.toString().toIntOrNull() ?: 0
        val telefono = etTelefono.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val genero = spGenero.selectedItem.toString()

        if (nombre.isEmpty() || apellidos.isEmpty() || tipoId.isEmpty() ||
            numeroId.isEmpty() || fecha.isEmpty() || genero.isEmpty()
        ) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return null
        }

        return Usuario(
            id = idOpt ?: 0,
            nombre = nombre,
            apellidos = apellidos,
            tipoId = tipoId,
            numeroId = numeroId,
            fechaNacimiento = fecha,
            edad = edad,
            telefono = telefono,
            correo = correo,
            direccion = direccion,
            genero = genero
        )
    }

    private fun limpiarFormulario() {
        seleccionadoId = null
        etNombre.text.clear()
        etApellidos.text.clear()
        spTipoId.setSelection(0)
        etNumeroId.text.clear()
        etFechaNacimiento.text.clear()
        etEdad.text.clear()
        etTelefono.text.clear()
        etCorreo.text.clear()
        etDireccion.text.clear()
        spGenero.setSelection(0)
        btnAgregarUsuario.text = "Agregar Usuario"
    }

    private fun llenarFormularioParaEdicion(u: Usuario) {
        seleccionadoId = u.id
        etNombre.setText(u.nombre)
        etApellidos.setText(u.apellidos)
        val indexTipo = tipos.indexOf(u.tipoId)
        if (indexTipo >= 0) spTipoId.setSelection(indexTipo)
        etNumeroId.setText(u.numeroId)
        etFechaNacimiento.setText(u.fechaNacimiento.split("-").reversed().joinToString("/"))
        etFechaNacimiento.tag = u.fechaNacimiento
        etEdad.setText(u.edad.toString())
        etTelefono.setText(u.telefono)
        etCorreo.setText(u.correo)
        etDireccion.setText(u.direccion)
        val indexGenero = generos.indexOf(u.genero)
        if (indexGenero >= 0) spGenero.setSelection(indexGenero)
        btnAgregarUsuario.text = "Actualizar Usuario"
    }

    private fun confirmarYEliminar(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Seguro que deseas eliminar este usuario?")
            .setPositiveButton("Sí") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val filas = dbHelper.eliminarUsuario(id)
                    withContext(Dispatchers.Main) {
                        if (filas > 0) {
                            Toast.makeText(this@MainActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(this@MainActivity, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cargarUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = dbHelper.listarUsuarios()
            withContext(Dispatchers.Main) {
                listaUsuarios.clear()
                listaUsuarios.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
