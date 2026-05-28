package com.example.tfgaplicacion.data

import androidx.room.*
import com.example.tfgaplicacion.model.RegistroDiario
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDiarioDao {
    @Query("SELECT * FROM registros_diarios WHERE adiccionId = :adiccionId ORDER BY fecha DESC")
    fun getRegistrosByAdiccion(adiccionId: Long): Flow<List<RegistroDiario>>

    @Query("SELECT * FROM registros_diarios WHERE adiccionId = :adiccionId AND fecha = :fecha LIMIT 1")
    suspend fun getRegistroByFecha(adiccionId: Long, fecha: Long): RegistroDiario?

    @Query("SELECT * FROM registros_diarios WHERE adiccionId = :adiccionId AND fecha BETWEEN :fechaInicio AND :fechaFin")
    fun getRegistrosBetweenDates(adiccionId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroDiario>>

    @Query("SELECT COUNT(*) FROM registros_diarios WHERE adiccionId = :adiccionId AND completado = 1")
    suspend fun getDiasCompletados(adiccionId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registro: RegistroDiario): Long

    @Update
    suspend fun update(registro: RegistroDiario)

    @Delete
    suspend fun delete(registro: RegistroDiario)
}