package com.example.tfgaplicacion.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tfgaplicacion.data.AppDatabase
import com.example.tfgaplicacion.data.SessionManager
import com.example.tfgaplicacion.data.UsuarioRepository
import com.example.tfgaplicacion.databinding.ActivityRegisterBinding
import com.example.tfgaplicacion.model.Usuario
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        usuarioRepository = UsuarioRepository(db.usuarioDao())
        sessionManager = SessionManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            binding.tilNombre.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            var valid = true
            if (nombre.isEmpty()) {
                binding.tilNombre.error = "Introduce tu nombre"
                valid = false
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Introduce un correo válido"
                valid = false
            }
            if (password.length < 6) {
                binding.tilPassword.error = "Mínimo 6 caracteres"
                valid = false
            }
            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
                valid = false
            }
            if (!valid) return@setOnClickListener

            lifecycleScope.launch {
                if (usuarioRepository.existeEmail(email)) {
                    runOnUiThread {
                        binding.tilEmail.error = "Este correo ya está registrado"
                    }
                    return@launch
                }

                val usuario = Usuario(
                    nombre = nombre,
                    email = email,
                    password = password,
                    fechaRegistro = System.currentTimeMillis()
                )
                val id = usuarioRepository.insertUsuario(usuario)
                sessionManager.saveSession(id)

                runOnUiThread {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
