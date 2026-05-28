package com.example.tfgaplicacion.data

import androidx.room.*
import com.example.tfgaplicacion.model.Insignia
import com.example.tfgaplicacion.model.ProgresoUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoDao {
    @Query("SELECT * FROM progreso_usuario WHERE usuarioId = :usuarioId")
    fun getProgreso(usuarioId: Long): Flow<ProgresoUsuario?>

    @Query("SELECT * FROM progreso_usuario WHERE usuarioId = :usuarioId")
    suspend fun getProgresoSync(usuarioId: Long): ProgresoUsuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgreso(progreso: ProgresoUsuario)

    @Update
    suspend fun updateProgreso(progreso: ProgresoUsuario)

    @Query("SELECT * FROM insignia")
    fun getAllInsignias(): Flow<List<Insignia>>

    @Query("SELECT * FROM insignia WHERE id = :id")
    suspend fun getInsigniaById(id: String): Insignia?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsignia(insignia: Insignia)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllInsignias(insignias: List<Insignia>)

    @Query("SELECT COUNT(*) FROM insignia")
    suspend fun getInsigniasCount(): Int
}
