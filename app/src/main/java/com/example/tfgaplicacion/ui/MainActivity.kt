package com.example.tfgaplicacion.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgaplicacion.databinding.DialogInsigniasBinding
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.AdiccionRepository
import com.example.tfgaplicacion.data.ProgresoRepository
import com.example.tfgaplicacion.data.SessionManager
import com.example.tfgaplicacion.databinding.ActivityMainBinding
import com.example.tfgaplicacion.databinding.DialogAgregarAdiccionBinding
import com.example.tfgaplicacion.model.Adiccion
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: AdiccionRepository
    private lateinit var progresoRepository: ProgresoRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AdiccionAdapter
    private var userId: Long = 0

    data class TipoAdiccion(
        val nombre: String,
        val color: String,
        val colorFondo: String,
        val icono: String,
        val categoria: String
    )

    private val adiccionesPredeterminadas = listOf(
        TipoAdiccion("Nicotina", "#7B1FA2", "#F3E5F5", "nicotina", "sustancias"),
        TipoAdiccion("Alcohol", "#6A1B9A", "#EDE7F6", "alcohol", "sustancias"),
        TipoAdiccion("Azúcar", "#9C27B0", "#F8F0FC", "azucar", "sustancias"),
        TipoAdiccion("Videojuegos", "#1565C0", "#E3F2FD", "videojuegos", "digital"),
        TipoAdiccion("Redes Sociales", "#1976D2", "#E8EAF6", "redes", "digital"),
        TipoAdiccion("Internet", "#42A5F5", "#E1F5FE", "internet", "digital"),
        TipoAdiccion("Compras", "#388E3C", "#E8F5E9", "compras", "comportamental"),
        TipoAdiccion("Trabajo", "#2E7D32", "#E0F2F1", "trabajo", "comportamental")
    )

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUsuarioId()

        if (!sessionManager.isLoggedIn()) {
            goToLogin()
            return
        }

        val db = AppDatabase.getDatabase(this)
        repository = AdiccionRepository(db.adiccionDao(), db.registroDiarioDao(), userId)
        progresoRepository = ProgresoRepository(db.progresoDao(), userId)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupClickListeners()
        observeAdicciones()
        observeProgreso()
        inicializarProgreso()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                confirmarLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmarLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres salir?")
            .setPositiveButton("Cerrar sesión") { _, _ -> logout() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
        sessionManager.clearSession()
        AppDatabase.clearInstance()
        goToLogin()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun observeProgreso() {
        lifecycleScope.launch {
            progresoRepository.getProgreso().collectLatest { progreso ->
                progreso?.let { actualizarUIProgreso(it) }
            }
        }
    }

    private fun actualizarUIProgreso(progreso: com.example.tfgaplicacion.model.ProgresoUsuario) {
        val rango = com.example.tfgaplicacion.model.RangoLoL.getRangoActual(progreso.puntosTotales)

        binding.tvRangoNombre.text = "${rango.nombre} ${getNumeroRomano(rango.nivel)}"
        binding.tvPuntos.text = "${progreso.puntosTotales} puntos"

        val progresoSiguiente = com.example.tfgaplicacion.model.RangoLoL.getProgresoHaciaSiguiente(progreso.puntosTotales)
        binding.progresoRango.progress = progresoSiguiente

        val siguiente = com.example.tfgaplicacion.model.RangoLoL.getSiguienteRango(progreso.puntosTotales)
        if (siguiente != null) {
            val falta = siguiente.puntosMinimos - progreso.puntosTotales
            binding.tvProgresoSiguiente.text = "$falta puntos hasta ${siguiente.nombre}"
        } else {
            binding.tvProgresoSiguiente.text = "¡Rango máximo alcanzado!"
        }

        val iconoRango = when (rango.nombre) {
            "HIERRO" -> R.drawable.ic_rango_hierro
            "BRONCE" -> R.drawable.ic_rango_bronce
            "PLATA" -> R.drawable.ic_rango_plata
            "ORO" -> R.drawable.ic_rango_oro
            "PLATINO" -> R.drawable.ic_rango_platino
            "DIAMANTE" -> R.drawable.ic_rango_diamante
            "MASTER" -> R.drawable.ic_rango_master
            "GRANDMASTER" -> R.drawable.ic_rango_grandmaster
            "CHALLENGER" -> R.drawable.ic_rango_challenger
            else -> R.drawable.ic_rango_hierro
        }
        binding.ivRango.setImageResource(iconoRango)
    }

    private fun getNumeroRomano(numero: Int): String = when (numero) {
        4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> ""
    }

    private fun inicializarProgreso() {
        lifecycleScope.launch {
            progresoRepository.initProgreso()
            progresoRepository.initInsignias()
        }
    }

    private fun setupRecyclerView() {
        adapter = AdiccionAdapter { adiccion ->
            val intent = Intent(this, AdiccionDetailActivity::class.java)
            intent.putExtra("adiccion_id", adiccion.id)
            startActivity(intent)
        }
        binding.recyclerAdicciones.layoutManager = LinearLayoutManager(this)
        binding.recyclerAdicciones.adapter = adapter

        val adiccionesGrid = listOf(
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Nicotina", R.drawable.ic_nicotina, "#7B1FA2", "#F3E5F5"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Alcohol", R.drawable.ic_alcohol, "#6A1B9A", "#EDE7F6"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Azúcar", R.drawable.ic_azucar, "#9C27B0", "#F8F0FC"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Videojuegos", R.drawable.ic_videojuegos, "#1565C0", "#E3F2FD"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Redes", R.drawable.ic_redes, "#1976D2", "#E8EAF6"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Internet", R.drawable.ic_internet, "#42A5F5", "#E1F5FE"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Compras", R.drawable.ic_compras, "#388E3C", "#E8F5E9"),
            AdiccionPredeterminadaAdapter.AdiccionPredeterminada("Trabajo", R.drawable.ic_trabajo, "#2E7D32", "#E0F2F1")
        )

        val adapterGrid = AdiccionPredeterminadaAdapter(adiccionesGrid) { item ->
            val tipo = adiccionesPredeterminadas.find {
                it.nombre.equals(item.nombre, ignoreCase = true) ||
                it.icono.equals(item.nombre.lowercase(), ignoreCase = true)
            } ?: adiccionesPredeterminadas.find { it.icono == getIconoString(item.nombre) }

            if (tipo != null) {
                mostrarDialogoFechaParaPredeterminada(tipo)
            } else {
                Toast.makeText(this, "Adicción no encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerAdiccionesPredeterminadas.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerAdiccionesPredeterminadas.adapter = adapterGrid
    }

    private fun getIconoString(nombre: String): String = when (nombre.lowercase()) {
        "nicotina" -> "nicotina"
        "alcohol" -> "alcohol"
        "azúcar", "azucar" -> "azucar"
        "videojuegos" -> "videojuegos"
        "redes", "redes sociales" -> "redes"
        "internet" -> "internet"
        "compras" -> "compras"
        "trabajo" -> "trabajo"
        else -> "personalizado"
    }

    private fun setupClickListeners() {
        binding.btnAgregar.setOnClickListener {
            mostrarDialogoTipoAgregar()
        }

        binding.btnInsignias.setOnClickListener {
            mostrarDialogoInsignias()
        }

        binding.btnLeyenda.setOnClickListener {
            startActivity(Intent(this, LeyendaActivity::class.java))
        }
    }

    private fun mostrarDialogoInsignias() {
        lifecycleScope.launch {
            val progreso = progresoRepository.getProgresoSync()
            val desbloqueadas = progreso?.insigniasDesbloqueadas
                ?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()

            val lista = progresoRepository.getAllInsignias().first()
            val total = lista.size
            val conseguidas = lista.count { ins -> desbloqueadas.contains(ins.id) }

            val dialogBinding = DialogInsigniasBinding.inflate(LayoutInflater.from(this@MainActivity))
            dialogBinding.tvResumenInsignias.text = "$conseguidas / $total desbloqueadas"

            val insigniaAdapter = InsigniaAdapter(lista, desbloqueadas)
            dialogBinding.rvInsignias.layoutManager = LinearLayoutManager(this@MainActivity)
            dialogBinding.rvInsignias.adapter = insigniaAdapter

            val dialog = MaterialAlertDialogBuilder(this@MainActivity)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.btnCerrar.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    // ─── Diálogos de creación de adicciones ──────────────────────────────────

    private fun mostrarDialogoTipoAgregar() {
        val opciones = arrayOf("Adicción predeterminada", "Adicción personalizada")
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Qué quieres añadir?")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoAgregarPredeterminada()
                    1 -> mostrarDialogoAgregarPersonalizada()
                }
            }
            .show()
    }

    private fun mostrarDialogoAgregarPredeterminada() {
        val dialogBinding = DialogAgregarAdiccionBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tvTituloDialog.text = "Adicción predeterminada"

        // Mostrar Spinner, ocultar campo personalizado
        dialogBinding.tvLabelSpinner.visibility = View.VISIBLE
        dialogBinding.spinnerPredeterminada.visibility = View.VISIBLE
        dialogBinding.tilNombrePersonalizado.visibility = View.GONE

        val nombres = listOf("-- Selecciona una adicción --") + adiccionesPredeterminadas.map { it.nombre }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerPredeterminada.adapter = spinnerAdapter

        var fechaMs = startOfToday()
        dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSeleccionarFecha.setOnClickListener {
            mostrarDatePicker(fechaMs) { seleccion ->
                fechaMs = seleccion
                dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))
            }
        }

        dialogBinding.btnCancelar.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnGuardar.setOnClickListener {
            val pos = dialogBinding.spinnerPredeterminada.selectedItemPosition
            if (pos == 0 || pos == AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Selecciona una adicción de la lista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val tipo = adiccionesPredeterminadas[pos - 1]
            val meta = dialogBinding.etMeta.text.toString().toIntOrNull() ?: 30

            lifecycleScope.launch {
                val existente = repository.getAdiccionByNombre(tipo.nombre)
                if (existente != null) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ya tienes \"${tipo.nombre}\" registrada", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val adiccion = Adiccion(
                    usuarioId = userId,
                    nombre = tipo.nombre,
                    icono = tipo.icono,
                    color = tipo.color,
                    colorFondo = tipo.colorFondo,
                    fechaInicio = fechaMs,
                    metaDias = meta
                )
                val id = repository.insertAdiccion(adiccion)
                runOnUiThread {
                    dialog.dismiss()
                    val intent = Intent(this@MainActivity, AdiccionDetailActivity::class.java)
                    intent.putExtra("adiccion_id", id)
                    startActivity(intent)
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoAgregarPersonalizada() {
        val dialogBinding = DialogAgregarAdiccionBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tvTituloDialog.text = "Adicción personalizada"

        // Mostrar campo de texto, ocultar Spinner
        dialogBinding.tilNombrePersonalizado.visibility = View.VISIBLE
        dialogBinding.tvLabelSpinner.visibility = View.GONE
        dialogBinding.spinnerPredeterminada.visibility = View.GONE

        var fechaMs = startOfToday()
        dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSeleccionarFecha.setOnClickListener {
            mostrarDatePicker(fechaMs) { seleccion ->
                fechaMs = seleccion
                dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))
            }
        }

        dialogBinding.btnCancelar.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnGuardar.setOnClickListener {
            val nombre = dialogBinding.etNombrePersonalizado.text.toString().trim()
            val meta = dialogBinding.etMeta.text.toString().toIntOrNull() ?: 30

            dialogBinding.tilNombrePersonalizado.error = null

            if (nombre.isEmpty()) {
                dialogBinding.tilNombrePersonalizado.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }
            if (nombre.length < 2) {
                dialogBinding.tilNombrePersonalizado.error = "Mínimo 2 caracteres"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existente = repository.getAdiccionByNombre(nombre)
                if (existente != null) {
                    runOnUiThread {
                        dialogBinding.tilNombrePersonalizado.error = "Ya existe una adicción con este nombre"
                    }
                    return@launch
                }
                val color = getColorPersonalizado()
                val adiccion = Adiccion(
                    usuarioId = userId,
                    nombre = nombre,
                    icono = "personalizado",
                    color = color,
                    colorFondo = getColorFondoPersonalizado(color),
                    fechaInicio = fechaMs,
                    metaDias = meta
                )
                val id = repository.insertAdiccion(adiccion)
                runOnUiThread {
                    dialog.dismiss()
                    val intent = Intent(this@MainActivity, AdiccionDetailActivity::class.java)
                    intent.putExtra("adiccion_id", id)
                    startActivity(intent)
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoFechaParaPredeterminada(tipo: TipoAdiccion) {
        val dialogBinding = DialogAgregarAdiccionBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tvTituloDialog.text = tipo.nombre

        dialogBinding.tvLabelSpinner.visibility = View.GONE
        dialogBinding.spinnerPredeterminada.visibility = View.GONE
        dialogBinding.tilNombrePersonalizado.visibility = View.GONE

        var fechaMs = startOfToday()
        dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSeleccionarFecha.setOnClickListener {
            mostrarDatePicker(fechaMs) { seleccion ->
                fechaMs = seleccion
                dialogBinding.btnSeleccionarFecha.text = dateFormat.format(Date(fechaMs))
            }
        }

        dialogBinding.btnCancelar.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnGuardar.setOnClickListener {
            val meta = dialogBinding.etMeta.text.toString().toIntOrNull() ?: 30
            lifecycleScope.launch {
                val existente = repository.getAdiccionByNombre(tipo.nombre)
                if (existente != null) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ya tienes \"${tipo.nombre}\" registrada", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val adiccion = Adiccion(
                    usuarioId = userId,
                    nombre = tipo.nombre,
                    icono = tipo.icono,
                    color = tipo.color,
                    colorFondo = tipo.colorFondo,
                    fechaInicio = fechaMs,
                    metaDias = meta
                )
                val id = repository.insertAdiccion(adiccion)
                runOnUiThread {
                    dialog.dismiss()
                    val intent = Intent(this@MainActivity, AdiccionDetailActivity::class.java)
                    intent.putExtra("adiccion_id", id)
                    startActivity(intent)
                }
            }
        }

        dialog.show()
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun mostrarDatePicker(seleccionActual: Long, onSeleccion: (Long) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona la fecha de inicio")
            .setSelection(seleccionActual)
            .build()
        picker.addOnPositiveButtonClickListener { utcMs ->
            // MaterialDatePicker devuelve UTC midnight; convertir a medianoche local
            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utcCal.timeInMillis = utcMs
            val local = Calendar.getInstance().apply {
                set(utcCal.get(Calendar.YEAR), utcCal.get(Calendar.MONTH), utcCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onSeleccion(local.timeInMillis)
        }
        picker.show(supportFragmentManager, "date_picker")
    }

    private fun startOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getColorPersonalizado(): String {
        val colores = listOf("#E91E63", "#00BCD4", "#FF9800", "#9C27B0", "#3F51B5", "#009688", "#795548", "#607D8B")
        return colores.random()
    }

    private fun getColorFondoPersonalizado(color: String): String = when (color) {
        "#E91E63" -> "#FCE4EC"
        "#00BCD4" -> "#E0F7FA"
        "#FF9800" -> "#FFF3E0"
        "#9C27B0" -> "#F3E5F5"
        "#3F51B5" -> "#E8EAF6"
        "#009688" -> "#E0F2F1"
        "#795548" -> "#EFEBE9"
        "#607D8B" -> "#ECEFF1"
        else -> "#FAFAFA"
    }

    private fun observeAdicciones() {
        lifecycleScope.launch {
            repository.allAdicciones.collectLatest { adicciones ->
                adapter.submitList(adicciones)
            }
        }
    }
}
