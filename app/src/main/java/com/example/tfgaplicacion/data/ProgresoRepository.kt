package com.example.tfgaplicacion.data

import com.example.tfgaplicacion.model.Insignia
import com.example.tfgaplicacion.model.ProgresoUsuario
import com.example.tfgaplicacion.model.RangoLoL
import kotlinx.coroutines.flow.Flow

class ProgresoRepository(
    private val progresoDao: ProgresoDao,
    private val usuarioId: Long
) {
    fun getProgreso(): Flow<ProgresoUsuario?> = progresoDao.getProgreso(usuarioId)

    suspend fun getProgresoSync(): ProgresoUsuario? = progresoDao.getProgresoSync(usuarioId)

    suspend fun initProgreso() {
        if (progresoDao.getProgresoSync(usuarioId) == null) {
            progresoDao.insertProgreso(ProgresoUsuario(usuarioId = usuarioId))
        }
    }

    suspend fun initInsignias() {
        if (progresoDao.getInsigniasCount() == 0) {
            val insignias = listOf(
                Insignia("primera_adiccion", "Primera Adicción", "Añade tu primera adicción", "star", 0, "inicio", "adicciones=1"),
                Insignia("tres_adicciones", "Trío de Adicciones", "Añade 3 adicciones diferentes", "stars", 50, "inicio", "adicciones=3"),
                Insignia("cinco_adicciones", "Coleccionista", "Añade 5 adicciones diferentes", "collections", 100, "inicio", "adicciones=5"),

                Insignia("dia_1", "Primer Día", "Completa tu primer día sin recaídas", "check", 10, "dias", "dias=1"),
                Insignia("dia_7", "Semana de Victoria", "Completa 7 días seguidos", "flag", 50, "dias", "dias=7"),
                Insignia("dia_30", "Mes Completado", "Completa 30 días seguidos", "calendar_month", 150, "dias", "dias=30"),
                Insignia("dia_100", "Cien Días", "Alcanza los 100 días de abstinencia", "emoji_events", 500, "dias", "dias=100"),
                Insignia("dia_365", "Año Completo", "Un año entero sin recaídas", "military_tech", 1000, "dias", "dias=365"),

                Insignia("primera_meta", "Objetivo Cumplido", "Alcanza tu primera meta de días", "target", 75, "metas", "metas=1"),
                Insignia("cinco_metas", "Maestro de Metas", "Alcanza 5 metas diferentes", "workspace_premium", 200, "metas", "metas=5"),
                Insignia("diez_metas", "Leyenda", "Alcanza 10 metas diferentes", "diamond", 400, "metas", "metas=10"),

                Insignia("primera_recaida", "Caída y Levantada", "Tu primera recaída y sigue intentándolo", "heart_broken", 0, "especial", "recaidas=1"),
                Insignia("cinco_recaidas", "Perseverante", "5 recaídas pero nunca te rindes", "retry", 25, "especial", "recaidas=5"),
                Insignia("volvio_10_veces", "Incansable", "Vuelve al camino 10 veces", "loop", 50, "especial", "recaidas=10"),

                Insignia("hierro", "Rango: Hierro", "Alcanza el rango Hierro", "shield", 0, "rango", "rango=HIERRO"),
                Insignia("bronce", "Rango: Bronce", "Alcanza el rango Bronce", "shield", 50, "rango", "rango=BRONCE"),
                Insignia("plata", "Rango: Plata", "Alcanza el rango Plata", "shield", 100, "rango", "rango=PLATA"),
                Insignia("oro", "Rango: Oro", "Alcanza el rango Oro", "shield", 200, "rango", "rango=ORO"),
                Insignia("platino", "Rango: Platino", "Alcanza el rango Platino", "shield", 300, "rango", "rango=PLATINO"),
                Insignia("diamante", "Rango: Diamante", "Alcanza el rango Diamante", "shield", 500, "rango", "rango=DIAMANTE"),
                Insignia("master", "Maestro", "Alcanza el rango Master", "shield", 700, "rango", "rango=MASTER"),
                Insignia("grandmaster", "Gran Maestro", "Alcanza el rango Grandmaster", "shield", 900, "rango", "rango=GRANDMASTER"),
                Insignia("challenger", "Challenger", "El rango más alto", "shield", 1200, "rango", "rango=CHALLENGER")
            )
            progresoDao.insertAllInsignias(insignias)
        }
    }

    fun getAllInsignias(): Flow<List<Insignia>> = progresoDao.getAllInsignias()

    suspend fun addPuntos(cantidad: Int) {
        val progreso = getProgresoSync() ?: ProgresoUsuario(usuarioId = usuarioId)
        val nuevoProgreso = progreso.copy(
            puntosTotales = progreso.puntosTotales + cantidad,
            nivel = RangoLoL.getRangoActual(progreso.puntosTotales + cantidad).nombre,
            rango = RangoLoL.getRangoActual(progreso.puntosTotales + cantidad).nivel
        )
        progresoDao.updateProgreso(nuevoProgreso)
    }

    suspend fun addDias(dias: Int) {
        val progreso = getProgresoSync() ?: ProgresoUsuario(usuarioId = usuarioId)
        progresoDao.updateProgreso(progreso.copy(diasTotales = progreso.diasTotales + dias))
    }

    suspend fun addRacha(dias: Int) {
        val progreso = getProgresoSync() ?: ProgresoUsuario(usuarioId = usuarioId)
        progresoDao.updateProgreso(progreso.copy(rachasConsecutivas = maxOf(progreso.rachasConsecutivas, dias)))
    }

    suspend fun addAdiccionCompletada() {
        val progreso = getProgresoSync() ?: ProgresoUsuario(usuarioId = usuarioId)
        progresoDao.updateProgreso(progreso.copy(adiccionesCompletadas = progreso.adiccionesCompletadas + 1))
    }

    // Returns true if the insignia was newly unlocked (not previously earned)
    suspend fun desbloquearInsignia(insigniaId: String): Boolean {
        val progreso = getProgresoSync() ?: return false
        val insigniasActual = progreso.insigniasDesbloqueadas.split(",").filter { it.isNotEmpty() }.toMutableSet()
        if (!insigniasActual.contains(insigniaId)) {
            insigniasActual.add(insigniaId)
            progresoDao.updateProgreso(progreso.copy(insigniasDesbloqueadas = insigniasActual.joinToString(",")))
            val insignia = progresoDao.getInsigniaById(insigniaId)
            if (insignia != null && insignia.puntosRequeridos > 0) {
                addPuntos(insignia.puntosRequeridos)
            }
            return true
        }
        return false
    }

    suspend fun getInsigniaById(id: String): Insignia? = progresoDao.getInsigniaById(id)
}
