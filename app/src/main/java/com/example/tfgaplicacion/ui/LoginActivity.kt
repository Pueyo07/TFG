package com.example.tfgaplicacion.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.SessionManager
import com.example.tfgaplicacion.data.UsuarioRepository
import com.example.tfgaplicacion.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        usuarioRepository = UsuarioRepository(db.usuarioDao())
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            goToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            var valid = true
            if (email.isEmpty()) {
                binding.tilEmail.error = "Introduce tu correo"
                valid = false
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Introduce tu contraseña"
                valid = false
            }
            if (!valid) return@setOnClickListener

            lifecycleScope.launch {
                val usuario = usuarioRepository.login(email, password)
                if (usuario != null) {
                    sessionManager.saveSession(usuario.id)
                    goToMain()
                } else {
                    runOnUiThread {
                        binding.tilPassword.error = "Correo o contraseña incorrectos"
                    }
                }
            }
        }

        binding.btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
