package com.example.stashit

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.stashit.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedColor = "mint"

    private val colorMap = mapOf(
        "mint" to "#26A69A",
        "blue" to "#42A5F5",
        "orange" to "#FFA726",
        "purple" to "#AB47BC",
        "pink" to "#EC407A"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupColorSwatches()
        setupColorSwatchListeners()
        loadCurrentData()

        binding.ivBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveChanges() }
    }

    private fun setupColorSwatches() {
        binding.colorMint.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(colorMap["mint"]))
        binding.colorBlue.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(colorMap["blue"]))
        binding.colorOrange.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(colorMap["orange"]))
        binding.colorPurple.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(colorMap["purple"]))
        binding.colorPink.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(colorMap["pink"]))
    }

    private fun setupColorSwatchListeners() {
        binding.colorMint.setOnClickListener { selectColor("mint") }
        binding.colorBlue.setOnClickListener { selectColor("blue") }
        binding.colorOrange.setOnClickListener { selectColor("orange") }
        binding.colorPurple.setOnClickListener { selectColor("purple") }
        binding.colorPink.setOnClickListener { selectColor("pink") }
    }

    private fun selectColor(colorKey: String) {
        selectedColor = colorKey
        val hex = colorMap[colorKey] ?: colorMap["mint"]!!
        binding.avatarCircle.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(hex))
    }

    private fun loadCurrentData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nama = document.getString("nama") ?: ""
                    binding.etNama.setText(nama)
                    binding.tvEmail.text = document.getString("email") ?: "-"
                    binding.tvAvatarInitial.text = nama.firstOrNull()?.uppercase() ?: "?"

                    val colorKey = document.getString("avatar_color") ?: "mint"
                    selectColor(colorKey)

                    val createdAt = document.getTimestamp("created_at")
                    if (createdAt != null) {
                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        binding.tvJoinDate.text = sdf.format(createdAt.toDate())
                    } else {
                        binding.tvJoinDate.text = "Sebelum fitur ini tersedia"
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveChanges() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Sesi tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val namaBaru = binding.etNama.text.toString().trim()
        if (namaBaru.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSave.isEnabled = false

        val updates = hashMapOf<String, Any>(
            "nama" to namaBaru,
            "avatar_color" to selectedColor
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}