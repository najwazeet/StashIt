package com.example.stashit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.AddGoalActivity
import com.example.stashit.DetailEventActivity
import com.example.stashit.adapter.AcaraAdapter
import com.example.stashit.data.Acara
import com.example.stashit.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.util.Locale
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.stashit.NotificationsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyAcara = listOf(
            Acara("Trip ke Bandung", "Bandung, Jawa Barat", "12 Agu 2026", "Liburan", 2_000_000, 800_000),
            Acara("Konser Coldplay", "Jakarta", "3 Sep 2026", "Konser", 3_500_000, 3_150_000),
            Acara("Wisuda Sahabat", "Denpasar, Bali", "20 Jul 2026", "Acara", 500_000, 120_000)
        )

        binding.rvAcara.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAcara.adapter = AcaraAdapter(dummyAcara) {
            startActivity(Intent(requireContext(), DetailEventActivity::class.java))
        }

        val totalTerkumpul = dummyAcara.sumOf { it.nominalTerkumpul }
        val totalTarget = dummyAcara.sumOf { it.targetNominal }
        val persentase = if (totalTarget == 0L) 0
        else ((totalTerkumpul.toDouble() / totalTarget) * 100).toInt().coerceIn(0, 100)
        val sisa = (totalTarget - totalTerkumpul).coerceAtLeast(0)

        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalTabungan.text = "Rp ${rupiah.format(totalTerkumpul)}"
        binding.progressTotal.progress = persentase
        binding.tvProgressCaption.text = "$persentase% dari target • Rp ${rupiah.format(sisa)} lagi"

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
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}