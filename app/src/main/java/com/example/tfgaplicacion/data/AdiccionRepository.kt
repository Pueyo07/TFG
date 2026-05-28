package com.example.tfgaplicacion.data

import com.example.tfgaplicacion.model.Adiccion
import com.example.tfgaplicacion.model.RegistroDiario
import kotlinx.coroutines.flow.Flow

class AdiccionRepository(
    private val adiccionDao: AdiccionDao,
    private val registroDiarioDao: RegistroDiarioDao,
    private val usuarioId: Long = 0
) {
    val allAdicciones: Flow<List<Adiccion>> = adiccionDao.getAdiccionesByUsuario(usuarioId)

    fun getAdiccionesByUsuario(usuarioId: Long): Flow<List<Adiccion>> =
        adiccionDao.getAdiccionesByUsuario(usuarioId)

    suspend fun getAdiccionById(id: Long): Adiccion? = adiccionDao.getAdiccionById(id)

    suspend fun getAdiccionByNombre(nombre: String): Adiccion? =
        adiccionDao.getAdiccionByNombre(nombre, usuarioId)

    suspend fun existeAdiccionConNombre(nombre: String): Boolean =
        adiccionDao.existeAdiccionConNombre(nombre, usuarioId) > 0

    suspend fun insertAdiccion(adiccion: Adiccion): Long = adiccionDao.insert(adiccion)

    suspend fun updateAdiccion(adiccion: Adiccion) = adiccionDao.update(adiccion)

    suspend fun deleteAdiccion(adiccion: Adiccion) = adiccionDao.delete(adiccion)

    fun getRegistrosByAdiccion(adiccionId: Long): Flow<List<RegistroDiario>> =
        registroDiarioDao.getRegistrosByAdiccion(adiccionId)

    suspend fun getRegistroByFecha(adiccionId: Long, fecha: Long): RegistroDiario? =
        registroDiarioDao.getRegistroByFecha(adiccionId, fecha)

    fun getRegistrosBetweenDates(adiccionId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroDiario>> =
        registroDiarioDao.getRegistrosBetweenDates(adiccionId, fechaInicio, fechaFin)

    suspend fun getDiasCompletados(adiccionId: Long): Int = registroDiarioDao.getDiasCompletados(adiccionId)

    suspend fun insertRegistro(registro: RegistroDiario): Long = registroDiarioDao.insert(registro)

    suspend fun updateRegistro(registro: RegistroDiario) = registroDiarioDao.update(registro)
}
