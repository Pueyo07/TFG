package com.example.tfgaplicacion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progreso_usuario")
data class ProgresoUsuario(
    @PrimaryKey
    val usuarioId: Long = 0,
    val puntosTotales: Int = 0,
    val nivel: String = "HIERRO",
    val rango: Int = 4,
    val insigniasDesbloqueadas: String = "",
    val adiccionesCompletadas: Int = 0,
    val diasTotales: Int = 0,
    val rachasConsecutivas: Int = 0
)

@Entity(tableName = "insignia")
data class Insignia(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val descripcion: String,
    val icono: String,
    val puntosRequeridos: Int,
    val categoria: String,
    val requisitos: String
)

data class RangoLoL(
    val nombre: String,
    val nivel: Int,
    val puntosMinimos: Int,
    val color: String,
    val icono: String
) {
    companion object {
        // Thresholds calibrados a 10 pts/día:
        // Bronce ≈ 2 semanas · Plata ≈ 1 mes · Oro ≈ 2 meses · Challenger ≈ 15 meses
        val RANGOS = listOf(
            RangoLoL("HIERRO",      4,    0, "#5E5E5E", "iron"),
            RangoLoL("HIERRO",      3,   50, "#5E5E5E", "iron"),
            RangoLoL("HIERRO",      2,  100, "#5E5E5E", "iron"),
            RangoLoL("HIERRO",      1,  150, "#5E5E5E", "iron"),
            RangoLoL("BRONCE",      4,  200, "#CD7F32", "bronze"),
            RangoLoL("BRONCE",      3,  275, "#CD7F32", "bronze"),
            RangoLoL("BRONCE",      2,  350, "#CD7F32", "bronze"),
            RangoLoL("BRONCE",      1,  425, "#CD7F32", "bronze"),
            RangoLoL("PLATA",       4,  500, "#C0C0C0", "silver"),
            RangoLoL("PLATA",       3,  600, "#C0C0C0", "silver"),
            RangoLoL("PLATA",       2,  700, "#C0C0C0", "silver"),
            RangoLoL("PLATA",       1,  800, "#C0C0C0", "silver"),
            RangoLoL("ORO",         4,  900, "#FFD700", "gold"),
            RangoLoL("ORO",         3, 1050, "#FFD700", "gold"),
            RangoLoL("ORO",         2, 1200, "#FFD700", "gold"),
            RangoLoL("ORO",         1, 1350, "#FFD700", "gold"),
            RangoLoL("PLATINO",     4, 1500, "#00CED1", "platinum"),
            RangoLoL("PLATINO",     3, 1700, "#00CED1", "platinum"),
            RangoLoL("PLATINO",     2, 1900, "#00CED1", "platinum"),
            RangoLoL("PLATINO",     1, 2100, "#00CED1", "platinum"),
            RangoLoL("DIAMANTE",    4, 2300, "#B9F2FF", "diamond"),
            RangoLoL("DIAMANTE",    3, 2550, "#B9F2FF", "diamond"),
            RangoLoL("DIAMANTE",    2, 2800, "#B9F2FF", "diamond"),
            RangoLoL("DIAMANTE",    1, 3050, "#B9F2FF", "diamond"),
            RangoLoL("MASTER",      1, 3300, "#E6B8FF", "master"),
            RangoLoL("GRANDMASTER", 1, 3750, "#FF4757", "grandmaster"),
            RangoLoL("CHALLENGER",  1, 4500, "#FF0000", "challenger")
        )

        fun getRangoActual(puntos: Int): RangoLoL {
            var rangoActual = RANGOS[0]
            for (rango in RANGOS) {
                if (puntos >= rango.puntosMinimos) rangoActual = rango
            }
            return rangoActual
        }

        fun getSiguienteRango(puntos: Int): RangoLoL? {
            val indiceActual = RANGOS.indexOfLast { it.puntosMinimos <= puntos }
            val siguienteIndice = indiceActual + 1
            return if (siguienteIndice < RANGOS.size) RANGOS[siguienteIndice] else null
        }

        fun getProgresoHaciaSiguiente(puntos: Int): Int {
            val indiceActual = RANGOS.indexOfLast { it.puntosMinimos <= puntos }
            if (indiceActual < 0 || indiceActual >= RANGOS.size - 1) return 100
            val puntosInicio = RANGOS[indiceActual].puntosMinimos
            val puntosSig = RANGOS[indiceActual + 1].puntosMinimos
            val puntosEnRango = puntos - puntosInicio
            val rangoSize = puntosSig - puntosInicio
            return if (rangoSize > 0) ((puntosEnRango * 100) / rangoSize).coerceIn(0, 100) else 0
        }
    }
}
