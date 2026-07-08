package com.example.stashit.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.stashit.R
import com.example.stashit.data.AcaraWithTotal
import com.example.stashit.databinding.FragmentStatsBinding
import com.example.stashit.databinding.ItemLegendBinding
import com.example.stashit.repository.FirestoreRepository
import com.example.stashit.view.DonutSlice
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import android.content.Intent
import com.example.stashit.NotificationUiActivity

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val repository = FirestoreRepository()

    private val palette = listOf(
        Color.parseColor("#2c6956"),
        Color.parseColor("#78555e"),
        Color.parseColor("#645880"),
        Color.parseColor("#a8e6cf"),
        Color.parseColor("#e6a8c1"),
        Color.parseColor("#a8c1e6")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch {
            try {
                val acaraWithTotals = repository.getAcaraWithTotals()
                setupDonutChart(acaraWithTotals)
                setupLegend(acaraWithTotals)
                setupSummaryCards(acaraWithTotals)
            } catch (e: Exception) {
                // Gagal fetch, biarkan tampilan kosong/default
            }
        }
    }

    private fun setupDonutChart(list: List<AcaraWithTotal>) {
        val slices = list.mapIndexed { index, item ->
            DonutSlice(item.totalTerkumpul.toFloat(), palette[index % palette.size])
        }
        binding.donutChart.setData(slices)

        val total = list.sumOf { it.totalTerkumpul }
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalSaved.text = "Rp ${rupiah.format(total)}"
    }

    private fun setupLegend(list: List<AcaraWithTotal>) {
        val total = list.sumOf { it.totalTerkumpul }
        binding.legendContainer.removeAllViews()

        list.forEachIndexed { index, item ->
            val itemBinding = ItemLegendBinding.inflate(
                LayoutInflater.from(requireContext()), binding.legendContainer, false
            )
            val percent = if (total == 0L) 0
            else ((item.totalTerkumpul.toDouble() / total) * 100).toInt()

            itemBinding.tvLegendLabel.text = item.acara.nama_acara
            itemBinding.tvLegendPercent.text = "$percent%"
            itemBinding.dotColor.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(palette[index % palette.size])
            }
            binding.legendContainer.addView(itemBinding.root)
        }
    }

    private fun setupSummaryCards(list: List<AcaraWithTotal>) {
        val activeCount = list.count { !it.isCompleted }
        val completedCount = list.count { it.isCompleted }

        binding.tvAvgMonthly.text = "Rp 750rb" // TODO: hitung dari history asli kalau dibutuhkan
        binding.tvActiveGoals.text = activeCount.toString()
        binding.tvCompletedGoals.text = completedCount.toString()
    }

    private fun setupCardNavigation() {
        binding.cardCompletedGoals.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EventsCompletedFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.cardActiveGoals.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNav
            ).selectedItemId = R.id.nav_home
        }

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationUiActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}