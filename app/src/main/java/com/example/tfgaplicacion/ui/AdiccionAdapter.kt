package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.databinding.ItemAdiccionBinding
import com.example.tfgaplicacion.model.Adiccion
import java.util.concurrent.TimeUnit

class AdiccionAdapter(
    private val onItemClick: (Adiccion) -> Unit
) : ListAdapter<Adiccion, AdiccionAdapter.ViewHolder>(DiffCallback()) {

    private val iconMap = mapOf(
        "nicotina" to R.drawable.ic_nicotina,
        "alcohol" to R.drawable.ic_alcohol,
        "videojuegos" to R.drawable.ic_videojuegos,
        "azucar" to R.drawable.ic_azucar,
        "redes" to R.drawable.ic_redes,
        "internet" to R.drawable.ic_internet,
        "compras" to R.drawable.ic_compras,
        "trabajo" to R.drawable.ic_trabajo,
        "personalizado" to R.drawable.ic_personalizado
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdiccionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAdiccionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(adiccion: Adiccion) {
            binding.nombreAdiccion.text = adiccion.nombre

            val diasTranscurridos = calcularDiasTranscurridos(adiccion.fechaInicio)
            binding.diasConteo.text = "$diasTranscurridos días"

            val progreso = if (adiccion.metaDias > 0) {
                (diasTranscurridos * 100 / adiccion.metaDias).coerceAtMost(100)
            } else 0
            binding.progresoBar.progress = progreso

            binding.diasMeta.text = "Meta: ${adiccion.metaDias} días"

            val iconRes = iconMap[adiccion.icono] ?: R.drawable.ic_nicotina
            binding.iconoAdiccion.setImageResource(iconRes)

            try {
                val color = Color.parseColor(adiccion.color)
                val drawable = binding.iconoAdiccion.background
                if (drawable is GradientDrawable) {
                    drawable.setColor(color)
                } else {
                    val newDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(color)
                    }
                    binding.iconoAdiccion.background = newDrawable
                }
            } catch (e: Exception) {
                val newDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#6200EE"))
                }
                binding.iconoAdiccion.background = newDrawable
            }

            binding.root.setOnClickListener {
                onItemClick(adiccion)
            }
        }

        private fun calcularDiasTranscurridos(fechaInicio: Long): Int {
            val ahora = System.currentTimeMillis()
            val diferencia = ahora - fechaInicio
            return TimeUnit.MILLISECONDS.toDays(diferencia).toInt() + 1
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Adiccion>() {
        override fun areItemsTheSame(oldItem: Adiccion, newItem: Adiccion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Adiccion, newItem: Adiccion): Boolean {
            return oldItem == newItem
        }
    }
}