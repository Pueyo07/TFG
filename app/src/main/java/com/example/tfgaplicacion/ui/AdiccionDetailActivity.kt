package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.AdiccionRepository
import com.example.tfgaplicacion.data.ProgresoRepository
import com.example.tfgaplicacion.databinding.ActivityAdiccionDetailBinding
import com.example.tfgaplicacion.model.Adiccion
import com.example.tfgaplicacion.model.InfoAdicciones
import com.example.tfgaplicacion.data.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AdiccionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdiccionDetailBinding
    private lateinit var repository: AdiccionRepository
    private lateinit var progresoRepository: ProgresoRepository
    private var adiccionId: Long = 0
    private var adiccion: Adiccion? = null

    private var colorCategoria: String = "#7B1FA2"
    private var colorFondo: String = "#FAFAFA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdiccionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adiccionId = intent.getLongExtra("adiccion_id", 0)

        val db = AppDatabase.getDatabase(this)
        val userId = SessionManager(this).getUsuarioId()
        repository = AdiccionRepository(db.adiccionDao(), db.registroDiarioDao(), userId)
        progresoRepository = ProgresoRepository(db.progresoDao(), userId)

        setupToolbar()
        setupTabs()
        loadAdiccion()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Calendario"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Estadísticas"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showFragment(CalendarioFragment.newInstance(adiccionId))
                    1 -> showFragment(EstadisticasFragment.newInstance(adiccionId))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showFragment(fragment: Fragment) {
        val tag = when (fragment) {
            is CalendarioFragment -> "CALENDARIO"
            is EstadisticasFragment -> "ESTADISTICAS"
            else -> fragment.javaClass.simpleName
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenidoPestana, fragment, tag)
            .commit()
    }

    private fun loadAdiccion() {
        lifecycleScope.launch {
            adiccion = repository.getAdiccionById(adiccionId)
            adiccion?.let {
                binding.toolbar.title = it.nombre
                aplicarColoresPorCategoria(it.nombre, it.color)
                actualizarContador(it)
                cargarInformacionAdiccion(it.nombre)
            }

            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        }
    }

    private fun cargarInformacionAdiccion(nombre: String) {
        val info = InfoAdicciones.getInfo(nombre)

        binding.tvTituloBeneficios.text = info.tituloBeneficios
        binding.tvTituloEstrategias.text = info.tituloEstrategias

        binding.tvBeneficios.text = info.beneficios.joinToString("\n")
        binding.tvEstrategias.text = info.estrategias.joinToString("\n")

        binding.tvConsejo.text = info.consejosDiarios.random()
    }

    /**
     * NORMA: Aplicar colores según la categoría de la adicción
     * - SUSTANCIAS (Nicotina, Alcohol, Azúcar): Morados
     * - DIGITAL (Videojuegos, Redes, Internet): Azules
     * - COMPORTAMENTAL (Compras, Trabajo): Verdes
     */
    private fun aplicarColoresPorCategoria(nombre: String, colorAdiccion: String?) {
        val categoria = detectarCategoria(nombre)
        
        when (categoria) {
            "sustancias" -> {
                colorCategoria = colorAdiccion ?: "#7B1FA2"
                colorFondo = "#F3E5F5"
            }
            "digital" -> {
                colorCategoria = colorAdiccion ?: "#1565C0"
                colorFondo = "#E3F2FD"
            }
            "comportamental" -> {
                colorCategoria = colorAdiccion ?: "#388E3C"
                colorFondo = "#E8F5E9"
            }
            else -> {
                colorCategoria = colorAdiccion ?: "#7B1FA2"
                colorFondo = "#FAFAFA"
            }
        }

        window.decorView.setBackgroundColor(Color.parseColor(colorFondo))

        binding.toolbar.setBackgroundColor(Color.parseColor(colorCategoria))
        
        binding.tvDiasConteo.setTextColor(Color.parseColor(colorCategoria))
        
        binding.progresoContador.setIndicatorColor(Color.parseColor(colorCategoria))
        
        binding.btnRegistrarDia.backgroundTintList = 
            android.content.res.ColorStateList.valueOf(Color.parseColor(colorCategoria))
        
        binding.tabLayout.setSelectedTabIndicatorColor(Color.parseColor(colorCategoria))
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, android.R.color.darker_gray),
            Color.parseColor(colorCategoria)
        )
    }

    private fun detectarCategoria(nombre: String): String {
        val nombreLower = nombre.lowercase().replace("sociales", "").trim()
        return when (nombreLower) {
            "nicotina", "alcohol", "azúcar" -> "sustancias"
            "videojuegos", "redes", "internet" -> "digital"
            "compras", "trabajo" -> "comportamental"
            else -> "sustancias"
        }
    }

    private fun actualizarContador(adiccion: Adiccion) {
        val diasTranscurridos = calcularDiasTranscurridos(adiccion.fechaInicio)
        binding.tvDiasConteo.text = diasTranscurridos.toString()

        val progreso = if (adiccion.metaDias > 0) {
            (diasTranscurridos * 100 / adiccion.metaDias).coerceAtMost(100)
        } else 0
        binding.progresoContador.progress = progreso

        binding.tvMeta.text = "Meta: ${adiccion.metaDias} días"
    }

    private fun calcularDiasTranscurridos(fechaInicio: Long): Int {
        val ahora = System.currentTimeMillis()
        val diferencia = ahora - fechaInicio
        return TimeUnit.MILLISECONDS.toDays(diferencia).toInt() + 1
    }

    private fun setupClickListeners() {
        binding.btnRegistrarDia.setOnClickListener {
            mostrarDialogoRegistrarDia()
        }
    }

    private fun mostrarDialogoRegistrarDia() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Registrar día de hoy")
            .setMessage("¿Cómo te fue hoy?")
            .setPositiveButton("Día completado") { _, _ ->
                registrarDia(true)
            }
            .setNegativeButton("Recaí") { _, _ ->
                registrarDia(false)
            }
            .show()
    }

    private fun registrarDia(completado: Boolean) {
        lifecycleScope.launch {
            val hoy = getStartOfDay(System.currentTimeMillis())
            val registro = com.example.tfgaplicacion.model.RegistroDiario(
                adiccionId = adiccionId,
                fecha = hoy,
                completado = completado
            )
            repository.insertRegistro(registro)
            
            if (completado) {
                progresoRepository.initProgreso()
                progresoRepository.addPuntos(10)
                progresoRepository.addDias(1)
                
                val progreso = progresoRepository.getProgresoSync()
                progreso?.let {
                    progresoRepository.addRacha(it.diasTotales + 1)
                    verificarInsignias(it.diasTotales + 1)
                }
            }
            
            val mensaje = if (completado) "¡Día completado! +10 puntos" else "Día registrado. ¡Ánimo, sigue intentándolo!"
            Toast.makeText(this@AdiccionDetailActivity, mensaje, Toast.LENGTH_SHORT).show()
            
            adiccion?.let { actualizarContador(it) }
        }
    }

    private suspend fun verificarInsignias(diasTotales: Int) {
        val nuevas = mutableListOf<String>()

        val diaInsignia = when (diasTotales) {
            1 -> "dia_1"; 7 -> "dia_7"; 30 -> "dia_30"; 100 -> "dia_100"; 365 -> "dia_365"; else -> null
        }
        diaInsignia?.let { if (progresoRepository.desbloquearInsignia(it)) nuevas.add(it) }

        val rango = com.example.tfgaplicacion.model.RangoLoL.getRangoActual(
            progresoRepository.getProgresoSync()?.puntosTotales ?: 0
        )
        val rangoInsignia = rango.nombre.lowercase()
        if (progresoRepository.desbloquearInsignia(rangoInsignia)) nuevas.add(rangoInsignia)

        if (nuevas.isNotEmpty()) {
            val insignia = progresoRepository.getInsigniaById(nuevas.first())
            val nombre = insignia?.nombre ?: "Nueva insignia"
            runOnUiThread {
                Snackbar.make(binding.root, "🏅 ¡Insignia desbloqueada! $nombre", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}