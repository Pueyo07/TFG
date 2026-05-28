package com.example.tfgaplicacion.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveSession(usuarioId: Long) {
        prefs.edit().putLong("usuario_id", usuarioId).apply()
    }

    fun getUsuarioId(): Long = prefs.getLong("usuario_id", -1L)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getUsuarioId() != -1L
}
