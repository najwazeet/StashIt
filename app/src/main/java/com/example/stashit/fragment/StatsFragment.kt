package com.example.stashit.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stashit.R
import com.example.stashit.databinding.FragmentStatsBinding
import com.example.stashit.databinding.ItemLegendBinding
import com.example.stashit.view.DonutSlice
import java.text.NumberFormat
import java.util.Locale
import android.content.Intent
import com.example.stashit.NotificationsActivity

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    data class AllocationStat(val label: String, val amount: Long, val color: Int)

    private val allocationStats = listOf(
        AllocationStat("Tiket Pesawat", 1_480_000, Color.parseColor("#2c6956")),
        AllocationStat("Penginapan", 1_200_000, Color.parseColor("#78555e")),
        AllocationStat("Konsumsi", 900_000, Color.parseColor("#645880")),
        AllocationStat("Transportasi", 650_000, Color.parseColor("#a8e6cf"))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDonutChart()
        setupLegend()
        setupSummaryCards()
        setupCardNavigation()
    }

    private fun setupDonutChart() {
        val slices = allocationStats.map { DonutSlice(it.amount.toFloat(), it.color) }
        binding.donutChart.setData(slices)

        val total = allocationStats.sumOf { it.amount }
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalSaved.text = "Rp ${rupiah.format(total)}"
    }

    private fun setupLegend() {
        val total = allocationStats.sumOf { it.amount }
        binding.legendContainer.removeAllViews()

        allocationStats.forEach { stat ->
            val itemBinding = ItemLegendBinding.inflate(
                LayoutInflater.from(requireContext()), binding.legendContainer, false
            )
            val percent = if (total == 0L) 0
            else ((stat.amount.toDouble() / total) * 100).toInt()

            itemBinding.tvLegendLabel.text = stat.label
            itemBinding.tvLegendPercent.text = "$percent%"
            itemBinding.dotColor.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(stat.color)
            }
            binding.legendContainer.addView(itemBinding.root)
        }
    }

    private fun setupSummaryCards() {
        // Dummy data ringkasan
        binding.tvAvgMonthly.text = "Rp 750rb"
        binding.tvActiveGoals.text = "3"
        binding.tvCompletedGoals.text = "12"
    }

    private fun setupCardNavigation() {
        // Klik card "Completed Goals" -> ke halaman Events Completed
        binding.cardCompletedGoals.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EventsCompletedFragment())
                .addToBackStack(null)
                .commit()
        }

        // Klik card "Active Goals" -> pindah tab Home lewat Bottom Nav
        binding.cardActiveGoals.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNav
            ).selectedItemId = R.id.nav_home
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