package com.example.tfgaplicacion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.AdiccionRepository
import com.example.tfgaplicacion.databinding.FragmentCalendarioBinding
import com.example.tfgaplicacion.model.RegistroDiario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarioFragment : Fragment() {
    private var _binding: FragmentCalendarioBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AdiccionRepository
    private var adiccionId: Long = 0
    private var fechaSeleccionada: Long = 0
    private var registroActual: RegistroDiario? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        private const val ARG_ADICCION_ID = "adiccion_id"

        fun newInstance(adiccionId: Long): CalendarioFragment {
            return CalendarioFragment().apply {
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
        _binding = FragmentCalendarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fechaSeleccionada = getStartOfDay(System.currentTimeMillis())
        binding.tvFechaSeleccionada.text = "Fecha: ${dateFormat.format(Date(fechaSeleccionada))}"

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            fechaSeleccionada = getStartOfDay(calendar.timeInMillis)
            binding.tvFechaSeleccionada.text = "Fecha: ${dateFormat.format(Date(fechaSeleccionada))}"
            cargarRegistroDelDia()
        }

        binding.btnGuardarRegistro.setOnClickListener {
            guardarRegistro()
        }

        cargarRegistroDelDia()
    }

    private fun cargarRegistroDelDia() {
        lifecycleScope.launch {
            registroActual = repository.getRegistroByFecha(adiccionId, fechaSeleccionada)
            registroActual?.let { registro ->
                binding.etNota.setText(registro.nota)
                binding.sliderAnsiedad.value = registro.nivelAnsiedad.toFloat()

                if (registro.completado) {
                    binding.btnDiaCompletado.isChecked = true
                } else {
                    binding.btnDiaFallido.isChecked = true
                }
            } ?: run {
                binding.etNota.setText("")
                binding.sliderAnsiedad.value = 5f
                binding.btnDiaCompletado.isChecked = false
                binding.btnDiaFallido.isChecked = false
            }
        }
    }

    private fun guardarRegistro() {
        val completado = binding.btnDiaCompletado.isChecked
        val nota = binding.etNota.text.toString()
        val nivelAnsiedad = binding.sliderAnsiedad.value.toInt()

        lifecycleScope.launch {
            val registro = RegistroDiario(
                id = registroActual?.id ?: 0,
                adiccionId = adiccionId,
                fecha = fechaSeleccionada,
                completado = completado,
                nota = nota,
                nivelAnsiedad = nivelAnsiedad
            )

            if (registroActual != null) {
                repository.updateRegistro(registro)
            } else {
                repository.insertRegistro(registro)
            }

            Toast.makeText(requireContext(), "Registro guardado", Toast.LENGTH_SHORT).show()
        }
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