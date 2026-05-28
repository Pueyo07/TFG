package com.example.tfgaplicacion.data

import com.example.tfgaplicacion.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    val allUsuarios: Flow<List<Usuario>> = usuarioDao.getAllUsuarios()

    suspend fun getUsuarioById(id: Long): Usuario? = usuarioDao.getUsuarioById(id)

    suspend fun getUsuarioByEmail(email: String): Usuario? = usuarioDao.getUsuarioByEmail(email)

    suspend fun login(email: String, password: String): Usuario? = usuarioDao.login(email, password)

    suspend fun insertUsuario(usuario: Usuario): Long = usuarioDao.insert(usuario)

    suspend fun updateUsuario(usuario: Usuario) = usuarioDao.update(usuario)

    suspend fun deleteUsuario(usuario: Usuario) = usuarioDao.delete(usuario)

    suspend fun existeEmail(email: String): Boolean = usuarioDao.existeEmail(email) > 0
}