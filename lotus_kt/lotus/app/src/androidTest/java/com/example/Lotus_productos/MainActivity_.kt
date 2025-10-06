package com.example.Lotus_productos

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity_ : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper_
    private lateinit var etNombreUsuario: EditText
    private lateinit var etApellidosUsuario: EditText
    private lateinit var spinnerTipoId: Spinner
    private lateinit var etNumeroId: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var tvEdad: TextView
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etDireccion: EditText
    private lateinit var rgGenero: RadioGroup
    private lateinit var btnGuardarUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.Lotus_productos.R.layout.activity_usuarios)

        dbHelper = DbHelper_(this)

        // Vincular vistas
        etNombreUsuario = findViewById(com.example.Lotus_productos.R.id.etNombreUsuario)
        etApellidosUsuario = findViewById(com.example.Lotus_productos.R.id.etApellidosUsuario)
        spinnerTipoId = findViewById(com.example.Lotus_productos.R.id.spinnerTipoId)
        etNumeroId = findViewById(com.example.Lotus_productos.R.id.etNumeroId)
        etFechaNacimiento = findViewById(com.example.Lotus_productos.R.id.etFechaNacimiento)
        tvEdad = findViewById(com.example.Lotus_productos.R.id.tvEdad)
        etTelefono = findViewById(com.example.Lotus_productos.R.id.etTelefono)
        etCorreo = findViewById(com.example.Lotus_productos.R.id.etCorreo)
        etDireccion = findViewById(com.example.Lotus_productos.R.id.etDireccion)
        rgGenero = findViewById(com.example.Lotus_productos.R.id.rgGenero)
        btnGuardarUsuario = findViewById(com.example.Lotus_productos.R.id.btnGuardarUsuario)

        // Spinner de tipo de identificación
        val adapterSpinner = ArrayAdapter.createFromResource(
            this,
            com.example.Lotus_productos.R.array.tipos_identificacion,
            android.R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoId.adapter = adapterSpinner

        // Seleccionar fecha con DatePicker
        etFechaNacimiento.setOnClickListener { mostrarDatePicker() }

        // Guardar usuario
        btnGuardarUsuario.setOnClickListener {
            val usuario = leerFormulario() ?: return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                val id = dbHelper.insertarUsuario(usuario)
                withContext(Dispatchers.Main) {
                    if (id != -1L) {
                        Toast.makeText(this@MainActivity_, "Usuario registrado", Toast.LENGTH_SHORT).show()
                        limpiarFormulario()
                    } else {
                        Toast.makeText(this@MainActivity_, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // ---------- MÉTODOS AUXILIARES ----------

    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, y, m, d ->
            val fechaIso = String.format("%04d-%02d-%02d", y, m + 1, d)
            etFechaNacimiento.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            etFechaNacimiento.tag = fechaIso
            val edad = calcularEdad(y, m, d)
            tvEdad.text = "Edad: $edad"
            tvEdad.tag = edad
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

    private fun leerFormulario(): Usuario? {
        val nombre = etNombreUsuario.text.toString().trim()
        val apellidos = etApellidosUsuario.text.toString().trim()
        val tipoId = spinnerTipoId.selectedItem.toString()
        val numeroId = etNumeroId.text.toString().trim()
        val fecha = etFechaNacimiento.tag as? String ?: ""
        val edad = tvEdad.tag as? Int ?: 0
        val telefono = etTelefono.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val genero = when (rgGenero.checkedRadioButtonId) {
            com.example.Lotus_productos.R.id.rbMasculino -> "Masculino"
            com.example.Lotus_productos.R.id.rbFemenino -> "Femenino"
            com.example.Lotus_productos.R.id.rbOtro -> "Otro"
            else -> ""
        }

        if (nombre.isEmpty() || apellidos.isEmpty() || numeroId.isEmpty() || fecha.isEmpty() || genero.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return null
        }

        return Usuario(
            id = 0,
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
        etNombreUsuario.text.clear()
        etApellidosUsuario.text.clear()
        spinnerTipoId.setSelection(0)
        etNumeroId.text.clear()
        etFechaNacimiento.text.clear()
        tvEdad.text = "Edad:"
        tvEdad.tag = 0
        etTelefono.text.clear()
        etCorreo.text.clear()
        etDireccion.text.clear()
        rgGenero.clearCheck()
    }
}
