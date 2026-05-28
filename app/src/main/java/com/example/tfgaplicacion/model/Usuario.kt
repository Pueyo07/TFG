package com.example.tfgaplicacion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaRegistro: Long,
    val fotoPerfil: String? = null
)