package com.example.stashit.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stashit.AboutActivity
import com.example.stashit.EditProfileActivity
import com.example.stashit.LoginActivity
import com.example.stashit.NotificationsActivity
import com.example.stashit.R
import com.example.stashit.SecurityActivity
import com.example.stashit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val colorMap = mapOf(
        "mint" to "#26A69A",
        "blue" to "#42A5F5",
        "orange" to "#FFA726",
        "purple" to "#AB47BC",
        "pink" to "#EC407A"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()
        setupMenuItems()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadUserProfile()
        }
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            goToLogin()
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (_binding == null) return@addOnSuccessListener
                if (document.exists()) {
                    val nama = document.getString("nama") ?: "-"
                    val email = document.getString("email") ?: "-"
                    binding.tvNama.text = nama
                    binding.tvEmail.text = email
                    binding.tvAppName.text = nama.split(" ").firstOrNull() ?: nama

                    binding.tvAvatarInitial.text = nama.firstOrNull()?.uppercase() ?: "?"
                    val colorKey = document.getString("avatar_color") ?: "mint"
                    val hex = colorMap[colorKey] ?: colorMap["mint"]!!
                    binding.avatarCircle.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor(hex))
                } else {
                    Toast.makeText(requireContext(), "Data profil tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                if (_binding == null) return@addOnFailureListener
                Toast.makeText(requireContext(), "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupMenuItems() {
        binding.itemEditProfile.tvMenuLabel.text = "Edit Profile"
        binding.itemEditProfile.ivMenuIcon.setImageResource(R.drawable.ic_person)
        binding.itemEditProfile.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemNotifications.tvMenuLabel.text = "Notifications"
        binding.itemNotifications.ivMenuIcon.setImageResource(R.drawable.ic_bell)
        binding.itemNotifications.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemSecurity.tvMenuLabel.text = "Security"
        binding.itemSecurity.ivMenuIcon.setImageResource(R.drawable.ic_lock)
        binding.itemSecurity.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemAbout.tvMenuLabel.text = "About"
        binding.itemAbout.ivMenuIcon.setImageResource(android.R.drawable.ic_menu_info_details)
        binding.itemAbout.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemEditProfile.llMenuItem.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.itemNotifications.llMenuItem.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.itemSecurity.llMenuItem.setOnClickListener {
            startActivity(Intent(requireContext(), SecurityActivity::class.java))
        }
        binding.itemAbout.llMenuItem.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            goToLogin()
        }

        binding.ivEditBadge.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}