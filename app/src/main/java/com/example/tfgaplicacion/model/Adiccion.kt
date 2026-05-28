package com.example.tfgaplicacion.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "adicciones",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Adiccion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long = 1,
    val nombre: String,
    val icono: String,
    val color: String,
    val colorFondo: String = "#F3E5F5",
    val fechaInicio: Long,
    val metaDias: Int = 30
)