package com.example.tfgaplicacion.data

import androidx.room.*
import com.example.tfgaplicacion.model.Adiccion
import kotlinx.coroutines.flow.Flow

@Dao
interface AdiccionDao {
    @Query("SELECT * FROM adicciones WHERE usuarioId = :usuarioId ORDER BY nombre")
    fun getAdiccionesByUsuario(usuarioId: Long): Flow<List<Adiccion>>

    @Query("SELECT * FROM adicciones ORDER BY nombre")
    fun getAllAdicciones(): Flow<List<Adiccion>>

    @Query("SELECT * FROM adicciones WHERE id = :id")
    suspend fun getAdiccionById(id: Long): Adiccion?

    @Query("SELECT * FROM adicciones WHERE LOWER(nombre) = LOWER(:nombre) AND usuarioId = :usuarioId LIMIT 1")
    suspend fun getAdiccionByNombre(nombre: String, usuarioId: Long): Adiccion?

    @Query("SELECT COUNT(*) FROM adicciones WHERE LOWER(nombre) = LOWER(:nombre) AND usuarioId = :usuarioId")
    suspend fun existeAdiccionConNombre(nombre: String, usuarioId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(adiccion: Adiccion): Long

    @Update
    suspend fun update(adiccion: Adiccion)

    @Delete
    suspend fun delete(adiccion: Adiccion)
}
