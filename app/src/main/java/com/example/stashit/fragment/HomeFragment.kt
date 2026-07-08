package com.example.stashit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.AddGoalActivity
import com.example.stashit.DetailEventActivity
import com.example.stashit.NotificationUiActivity
import com.example.stashit.adapter.AcaraAdapter
import com.example.stashit.data.AcaraUiModel
import com.example.stashit.databinding.FragmentHomeBinding
import com.example.stashit.repository.FirestoreRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAcara.layoutManager = LinearLayoutManager(requireContext())

        loadUserName()
        loadAcaraData()

        binding.fabAddGoal.setOnClickListener {
            startActivity(Intent(requireContext(), AddGoalActivity::class.java))
        }

        // Sesuaikan posisi FAB otomatis berdasarkan tinggi Bottom Nav
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(
            resources.getIdentifier("bottomNav", "id", requireContext().packageName)
        )
        bottomNav?.post {
            val params = binding.fabAddGoal.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = bottomNav.height + 24
            binding.fabAddGoal.layoutParams = params
        }

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationUiActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserName()
        loadAcaraData()
    }

    private fun loadUserName() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (_binding == null) return@addOnSuccessListener // fragment view sudah destroyed
                val nama = document.getString("nama") ?: "Pengguna"
                binding.tvGreeting.text = "Halo, $nama!"
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvGreeting.text = "Halo!"
            }
    }

    private fun loadAcaraData() {
        lifecycleScope.launch {
            try {
                val acaraList = repository.getAcaraList()

                val uiModelList = acaraList.map { acara ->
                    val rincianList = repository.getRincianBiayaList(acara.idAcara)
                    val totalTerkumpul = rincianList.sumOf { it.nominal_terkumpul }
                    val totalTarget = rincianList.sumOf { it.target_nominal }
                    AcaraUiModel(acara, totalTerkumpul, totalTarget)
                }

                binding.rvAcara.adapter = AcaraAdapter(uiModelList) { acara ->
                    val intent = Intent(requireContext(), DetailEventActivity::class.java)
                    intent.putExtra("id_acara", acara.idAcara)
                    startActivity(intent)
                }

                val totalTerkumpul = uiModelList.sumOf { it.totalTerkumpul }
                val totalTarget = uiModelList.sumOf { it.totalTarget }
                val persentase = if (totalTarget == 0.0) 0
                else ((totalTerkumpul / totalTarget) * 100).toInt().coerceIn(0, 100)
                val sisa = (totalTarget - totalTerkumpul).coerceAtLeast(0.0)

                val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
                binding.tvTotalTabungan.text = "Rp ${rupiah.format(totalTerkumpul.toLong())}"
                binding.progressTotal.progress = persentase
                binding.tvProgressCaption.text =
                    "$persentase% dari target • Rp ${rupiah.format(sisa.toLong())} lagi"

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memuat data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}