package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.AdiccionRepository
import com.example.tfgaplicacion.databinding.FragmentEstadisticasBinding
import com.example.tfgaplicacion.model.Adiccion
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class EstadisticasFragment : Fragment() {
    private var _binding: FragmentEstadisticasBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AdiccionRepository
    private var adiccionId: Long = 0
    private var adiccion: Adiccion? = null

    companion object {
        private const val ARG_ADICCION_ID = "adiccion_id"

        fun newInstance(adiccionId: Long): EstadisticasFragment {
            return EstadisticasFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ADICCION_ID, adiccionId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adiccionId = arguments?.getLong(ARG_ADICCION_ID) ?: 0

        val db = AppDatabase.getDatabase(requireContext())
        repository = AdiccionRepository(db.adiccionDao(), db.registroDiarioDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarEstadisticas()
        setupClickListeners()
    }

    private fun cargarEstadisticas() {
        lifecycleScope.launch {
            adiccion = repository.getAdiccionById(adiccionId)

            adiccion?.let { adic ->
                val diasTotales = calcularDiasTranscurridos(adic.fechaInicio)
                binding.tvTotalDias.text = diasTotales.toString()

                binding.tvRachaActual.text = diasTotales.toString()
                binding.tvMejorRacha.text = diasTotales.toString()

                val registros = repository.getRegistrosByAdiccion(adiccionId).first()
                val diasCompletados = registros.count { it.completado }
                val porcentaje = if (registros.isNotEmpty()) {
                    (diasCompletados * 100 / registros.size)
                } else 0
                binding.tvPorcentajeExito.text = "$porcentaje%"

                dibujarGraficoSemanal(registros)
            }
        }
    }

    private fun calcularDiasTranscurridos(fechaInicio: Long): Int {
        val ahora = System.currentTimeMillis()
        val diferencia = ahora - fechaInicio
        val dias = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diferencia).toInt() + 1
        return dias.coerceAtLeast(0)
    }

    private fun dibujarGraficoSemanal(registros: List<com.example.tfgaplicacion.model.RegistroDiario>) {
        binding.layoutGraficoSemanal.removeAllViews()

        val calendar = Calendar.getInstance()
        val diasSemana = mutableListOf<Long>()

        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            diasSemana.add(getStartOfDay(calendar.timeInMillis))
        }

        val diasSemanaNombres = arrayOf("L", "M", "X", "J", "V", "S", "D")
        val calendarAux = Calendar.getInstance()

        diasSemana.forEachIndexed { index, fecha ->
            val registro = registros.find { it.fecha == fecha }
            val completado = registro?.completado ?: false

            val container = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                gravity = android.view.Gravity.CENTER_HORIZONTAL
            }

            val barra = View(requireContext()).apply {
                val height = if (completado) 80 else 20
                layoutParams = LinearLayout.LayoutParams(24.dpToPx(), height.dpToPx())
                setBackgroundColor(
                    if (completado) Color.parseColor("#4CAF50")
                    else Color.parseColor("#FFCDD2")
                )
            }

            val label = TextView(requireContext()).apply {
                text = diasSemanaNombres[index]
                textSize = 12f
                setTextColor(Color.GRAY)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 4.dpToPx()
                }
            }

            container.addView(barra)
            container.addView(label)
            binding.layoutGraficoSemanal.addView(container)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun setupClickListeners() {
        binding.btnConfigurarMeta.setOnClickListener {
            mostrarDialogoConfigurarMeta()
        }

        binding.btnReiniciarContador.setOnClickListener {
            mostrarDialogoReiniciar()
        }

        binding.btnEliminarAdiccion.setOnClickListener {
            mostrarDialogoEliminar()
        }
    }

    private fun mostrarDialogoConfigurarMeta() {
        val input = TextInputEditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(adiccion?.metaDias?.toString() ?: "30")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Configurar meta")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaMeta = input.text.toString().toIntOrNull() ?: 30
                lifecycleScope.launch {
                    adiccion?.let {
                        val actualizada = it.copy(metaDias = nuevaMeta)
                        repository.updateAdiccion(actualizada)
                        adiccion = actualizada
                        Toast.makeText(requireContext(), "Meta actualizada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoReiniciar() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reiniciar contador")
            .setMessage("¿Estás seguro de reiniciar el contador? Esto cambiará la fecha de inicio a hoy.")
            .setPositiveButton("Sí") { _, _ ->
                lifecycleScope.launch {
                    adiccion?.let {
                        val reiniciada = it.copy(fechaInicio = getStartOfDay(System.currentTimeMillis()))
                        repository.updateAdiccion(reiniciada)
                        adiccion = reiniciada
                        Toast.makeText(requireContext(), "Contador reiniciado", Toast.LENGTH_SHORT).show()
                        cargarEstadisticas()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarDialogoEliminar() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar adicción")
            .setMessage("¿Estás seguro de eliminar esta adicción? Se borrarán todos los registros.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    adiccion?.let {
                        repository.deleteAdiccion(it)
                        Toast.makeText(requireContext(), "Adicción eliminada", Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}