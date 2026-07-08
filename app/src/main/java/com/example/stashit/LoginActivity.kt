package com.example.stashit

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stashit.databinding.ActivityLoginBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Cek sesi aktif: kalau user udah login sebelumnya, langsung skip ke DaftarAcaraActivity
        if (auth.currentUser != null) {
            goToDaftarAcara()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        recordLoginSession(uid)
                    }
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    goToDaftarAcara()
                }
                .addOnFailureListener { e ->
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Continue with Google diklik", Toast.LENGTH_SHORT).show()
            // TODO: Google Sign-In, ini butuh setup terpisah (bahas nanti kalau udah siap)
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: navigate ke halaman ForgotPasswordActivity
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun recordLoginSession(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val deviceId = android.provider.Settings.Secure.getString(
            contentResolver, android.provider.Settings.Secure.ANDROID_ID
        )
        val deviceName = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"

        val sessionData = hashMapOf(
            "deviceName" to deviceName,
            "loginTime" to Timestamp.now()
        )

        db.collection("users").document(uid).collection("sessions")
            .document(deviceId)
            .set(sessionData)
    }

    private fun goToDaftarAcara() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}