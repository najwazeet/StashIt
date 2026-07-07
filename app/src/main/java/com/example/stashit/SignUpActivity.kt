package com.example.stashit

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.InputType
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stashit.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupTermsText()
        setupListeners()
    }

    private fun setupTermsText() {
        val fullText = "By creating an account, you agree to our Terms of Service and Privacy Policy."
        val spannable = SpannableString(fullText)

        val termsStart = fullText.indexOf("Terms of Service")
        val termsEnd = termsStart + "Terms of Service".length
        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length

        val mintColor = ContextCompat.getColor(this, R.color.mint_primary)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@SignUpActivity, "Terms of Service diklik", Toast.LENGTH_SHORT).show()
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = mintColor
                ds.isUnderlineText = false
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@SignUpActivity, "Privacy Policy diklik", Toast.LENGTH_SHORT).show()
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = mintColor
                ds.isUnderlineText = false
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTerms.text = spannable
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

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

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!binding.cbTerms.isChecked) {
                Toast.makeText(this, "Kamu harus setuju Terms of Service dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnCreateAccount.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid == null) {
                        binding.btnCreateAccount.isEnabled = true
                        Toast.makeText(this, "Gagal mendapatkan UID user", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val userData = hashMapOf(
                        "nama" to name,
                        "email" to email,
                        "foto_profil" to ""
                    )

                    db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, DaftarAcaraActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            binding.btnCreateAccount.isEnabled = true
                            Toast.makeText(this, "Gagal menyimpan profil: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    binding.btnCreateAccount.isEnabled = true
                    Toast.makeText(this, "Gagal membuat akun: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Sign up dengan Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnApple.setOnClickListener {
            Toast.makeText(this, "Sign up dengan Apple", Toast.LENGTH_SHORT).show()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}