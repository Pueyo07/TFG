package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.ProgresoRepository
import com.example.tfgaplicacion.data.SessionManager
import com.example.tfgaplicacion.databinding.ActivityLeyendaBinding
import com.example.tfgaplicacion.model.RangoLoL
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeyendaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeyendaBinding
    private lateinit var progresoRepository: ProgresoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeyendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val userId = SessionManager(this).getUsuarioId()
        progresoRepository = ProgresoRepository(db.progresoDao(), userId)

        setupToolbar()
        observeProgreso()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeProgreso() {
        lifecycleScope.launch {
            progresoRepository.getProgreso().collectLatest { progreso ->
                progreso?.let {
                    val rango = RangoLoL.getRangoActual(it.puntosTotales)
                    val siguiente = RangoLoL.getSiguienteRango(it.puntosTotales)
                    val progresoPorcentaje = RangoLoL.getProgresoHaciaSiguiente(it.puntosTotales)

                    binding.tvTuRango.text = "${rango.nombre} ${getNumeroRomano(rango.nivel)}"
                    binding.tvTusPuntos.text = "${it.puntosTotales} puntos"
                    
                    binding.progresoRango.progress = progresoPorcentaje
                    
                    val colorRango = Color.parseColor(
                        when (rango.nombre) {
                            "HIERRO" -> "#5E5E5E"
                            "BRONCE" -> "#CD7F32"
                            "PLATA" -> "#C0C0C0"
                            "ORO" -> "#FFD700"
                            "PLATINO" -> "#00CED1"
                            "DIAMANTE" -> "#B9F2FF"
                            "MASTER" -> "#E6B8FF"
                            "GRANDMASTER" -> "#FF4757"
                            "CHALLENGER" -> "#FF0000"
                            else -> "#5E5E5E"
                        }
                    )
                    
                    binding.tvTuRango.setTextColor(colorRango)
                    binding.progresoRango.setIndicatorColor(colorRango)
                    
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
                    binding.ivTuRango.setImageResource(iconoRango)
                    
                    if (siguiente != null) {
                        val falta = siguiente.puntosMinimos - it.puntosTotales
                        binding.tvProgresoSig.text = "$falta puntos hasta ${siguiente.nombre} ${getNumeroRomano(siguiente.nivel)}"
                    } else {
                        binding.tvProgresoSig.text = "¡Has alcanzado el rango máximo!"
                    }
                }
            }
        }
    }

    private fun getNumeroRomano(numero: Int): String {
        return when (numero) {
            4 -> "IV"
            3 -> "III"
            2 -> "II"
            1 -> "I"
            else -> ""
        }
    }
}