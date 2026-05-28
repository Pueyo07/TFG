package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tfgaplicacion.R
import com.example.tfgaplicacion.databinding.ItemAdiccionesGridBinding

class AdiccionPredeterminadaAdapter(
    private val listaAdicciones: List<AdiccionPredeterminada>,
    private val onItemClick: (AdiccionPredeterminada) -> Unit
) : RecyclerView.Adapter<AdiccionPredeterminadaAdapter.ViewHolder>() {

    data class AdiccionPredeterminada(
        val nombre: String,
        val icono: Int,
        val color: String,
        val colorFondo: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdiccionesGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaAdicciones[position])
    }

    override fun getItemCount() = listaAdicciones.size

    inner class ViewHolder(
        private val binding: ItemAdiccionesGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(adiccion: AdiccionPredeterminada) {
            binding.tvNombre.text = adiccion.nombre
            binding.ivIcono.setImageResource(adiccion.icono)

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(adiccion.color))
            }
            binding.ivIcono.background = drawable

            binding.root.setOnClickListener {
                onItemClick(adiccion)
            }
        }
    }

    private fun getDrawableBinding(parent: ViewGroup): ItemAdiccionesGridBinding {
        return ItemAdiccionesGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }
}