package com.example.stashit

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stashit.databinding.ActivitySecurityBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SecurityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupMenuItems()
        setupListeners()
    }

    private fun setupMenuItems() {
        binding.itemChangePassword.tvMenuLabel.text = "Change Password"
        binding.itemChangePassword.ivMenuIcon.setImageResource(R.drawable.ic_lock)
        binding.itemChangePassword.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemChangeEmail.tvMenuLabel.text = "Change Email"
        binding.itemChangeEmail.ivMenuIcon.setImageResource(android.R.drawable.ic_dialog_email)
        binding.itemChangeEmail.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemTwoFactor.tvMenuLabel.text = "Two-Factor Authentication"
        binding.itemTwoFactor.ivMenuIcon.setImageResource(android.R.drawable.ic_lock_idle_lock)
        binding.itemTwoFactor.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemLoginActivity.tvMenuLabel.text = "Login Activity"
        binding.itemLoginActivity.ivMenuIcon.setImageResource(android.R.drawable.ic_menu_recent_history)
        binding.itemLoginActivity.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemDeleteAccount.tvMenuLabel.text = "Delete Account"
        binding.itemDeleteAccount.ivMenuIcon.setImageResource(android.R.drawable.ic_menu_delete)
        binding.itemDeleteAccount.ivMenuIcon.setColorFilter(Color.parseColor("#C62828"))
        binding.itemDeleteAccount.tvMenuLabel.setTextColor(Color.parseColor("#C62828"))
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.itemChangePassword.root.setOnClickListener { showChangePasswordDialog() }
        binding.itemChangeEmail.root.setOnClickListener { showChangeEmailDialog() }
        binding.itemDeleteAccount.root.setOnClickListener { showDeleteAccountConfirmation() }

        binding.itemLoginActivity.root.setOnClickListener {
            startActivity(Intent(this, LoginActivityHistory::class.java))
        }

        binding.itemTwoFactor.root.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Two-Factor Authentication")
                .setMessage(
                    "Fitur ini butuh Firebase project di plan Blaze (berbayar) " +
                            "karena verifikasi SMS berbayar per pesan, plus setup phone provider " +
                            "di Firebase Console yang belum dikonfigurasi di project ini.\n\n" +
                            "Belum aktif untuk sekarang."
                )
                .setPositiveButton("Oke", null)
                .show()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_input, null)
        val tvInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewValue = dialogView.findViewById<EditText>(R.id.etNewValue)

        tvInfo.text = "Masukkan password saat ini, lalu password baru"
        etNewValue.hint = "Password baru"
        etNewValue.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewValue.text.toString()

                if (currentPassword.isBlank() || newPassword.isBlank()) {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (newPassword.length < 6) {
                    Toast.makeText(this, "Password baru minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                reauthenticateAndRun(currentPassword) {
                    auth.currentUser?.updatePassword(newPassword)
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal ubah password: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangeEmailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_input, null)
        val tvInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewValue = dialogView.findViewById<EditText>(R.id.etNewValue)

        tvInfo.text = "Masukkan password saat ini, lalu email baru"
        etNewValue.hint = "Email baru"
        etNewValue.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle("Change Email")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newEmail = etNewValue.text.toString()

                if (currentPassword.isBlank() || newEmail.isBlank()) {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                reauthenticateAndRun(currentPassword) {
                    auth.currentUser?.verifyBeforeUpdateEmail(newEmail)
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Link verifikasi dikirim ke email baru", Toast.LENGTH_LONG).show()
                        }
                        ?.addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal ubah email: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Akun?")
            .setMessage("Tindakan ini permanen dan tidak bisa dibatalkan. Semua data kamu akan hilang.")
            .setPositiveButton("Hapus") { _, _ -> askPasswordThenDeleteAccount() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun askPasswordThenDeleteAccount() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_input, null)
        val tvInfo = dialogView.findViewById<TextView>(R.id.tvDialogInfo)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewValue = dialogView.findViewById<EditText>(R.id.etNewValue)

        tvInfo.text = "Masukkan password untuk konfirmasi penghapusan akun"
        etNewValue.visibility = View.GONE

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Password")
            .setView(dialogView)
            .setPositiveButton("Hapus Akun") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                if (currentPassword.isBlank()) {
                    Toast.makeText(this, "Password harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                reauthenticateAndRun(currentPassword) {
                    auth.currentUser?.delete()
                        ?.addOnSuccessListener {
                            Toast.makeText(this, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        ?.addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal hapus akun: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun reauthenticateAndRun(currentPassword: String, onSuccess: () -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Password salah: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}