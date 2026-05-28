package com.example.tfgaplicacion.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_diarios",
    foreignKeys = [
        ForeignKey(
            entity = Adiccion::class,
            parentColumns = ["id"],
            childColumns = ["adiccionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("adiccionId")]
)
data class RegistroDiario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adiccionId: Long,
    val fecha: Long,
    val completado: Boolean,
    val nota: String = "",
    val nivelAnsiedad: Int = 0
)