package com.example.tfgaplicacion.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tfgaplicacion.databinding.ItemInsigniaBinding
import com.example.tfgaplicacion.model.Insignia

class InsigniaAdapter(
    private val insignias: List<Insignia>,
    private val desbloqueadas: Set<String>
) : RecyclerView.Adapter<InsigniaAdapter.ViewHolder>() {

    private val emojiMap = mapOf(
        "primera_adiccion" to "🌟",
        "tres_adicciones"  to "✨",
        "cinco_adicciones" to "💫",
        "dia_1"            to "✅",
        "dia_7"            to "📅",
        "dia_30"           to "🗓️",
        "dia_100"          to "💯",
        "dia_365"          to "🏆",
        "primera_meta"     to "🎯",
        "cinco_metas"      to "🌙",
        "diez_metas"       to "👑",
        "primera_recaida"  to "❤️",
        "cinco_recaidas"   to "💪",
        "volvio_10_veces"  to "🔄",
        "hierro"           to "🛡️",
        "bronce"           to "🥉",
        "plata"            to "🥈",
        "oro"              to "🥇",
        "platino"          to "💠",
        "diamante"         to "💎",
        "master"           to "👑",
        "grandmaster"      to "⚡",
        "challenger"       to "🔥"
    )

    // Color de fondo del icono por categoría
    private val categoryColor = mapOf(
        "inicio"   to "#06B6D4",
        "dias"     to "#10B981",
        "metas"    to "#F59E0B",
        "especial" to "#EC4899",
        "rango"    to "#6366F1"
    )

    // Color del badge según puntos de recompensa
    private fun badgeColor(puntos: Int): String = when {
        puntos == 0    -> "#64748B"
        puntos < 100   -> "#10B981"
        puntos < 300   -> "#3B82F6"
        puntos < 700   -> "#F59E0B"
        else           -> "#8B5CF6"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInsigniaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(insignias[position])
    }

    override fun getItemCount() = insignias.size

    inner class ViewHolder(private val b: ItemInsigniaBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(insignia: Insignia) {
            val unlocked = desbloqueadas.contains(insignia.id)

            b.tvEmoji.text = emojiMap[insignia.id] ?: "🏅"
            b.tvNombre.text = insignia.nombre
            b.tvDescripcion.text = insignia.descripcion

            // Color del fondo del icono según categoría
            val bgColor = categoryColor[insignia.categoria] ?: "#6366F1"
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(bgColor))
            }
            b.iconBg.background = drawable

            // Badge: "Obtenida" si desbloqueada, o puntos de recompensa si no
            if (unlocked) {
                b.tvBadge.text = "✓ Obtenida"
                setDrawableColor(b.tvBadge, "#10B981")
            } else {
                b.tvBadge.text = if (insignia.puntosRequeridos > 0) "+${insignia.puntosRequeridos}pts" else "🔒"
                setDrawableColor(b.tvBadge, badgeColor(insignia.puntosRequeridos))
            }

            // Estado visual: bloqueado = fondo gris + semitransparente
            if (unlocked) {
                b.root.alpha = 1f
                b.tvEmoji.alpha = 1f
            } else {
                b.root.alpha = 0.4f
                b.tvEmoji.alpha = 0.5f
                // Sobreescribir fondo del icono con gris en bloqueado
                val grayBg = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#475569"))
                }
                b.iconBg.background = grayBg
            }
        }

        private fun setDrawableColor(view: android.widget.TextView, hex: String) {
            val bg = view.background
            if (bg is GradientDrawable) {
                bg.setColor(Color.parseColor(hex))
            } else {
                val newBg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 40f
                    setColor(Color.parseColor(hex))
                }
                view.background = newBg
            }
        }
    }
}
