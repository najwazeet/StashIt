package com.example.stashit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.SavingsHistoryAdapter
import com.example.stashit.data.SavingsHistory
import com.example.stashit.databinding.ActivityAllocationDetailBinding
import java.text.NumberFormat
import java.util.Locale
import android.content.Intent

class AllocationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllocationDetailBinding

    private val targetNominal = 1_500_000L

    private val historyList = listOf(
        SavingsHistory("Setoran Tabungan", "5 Jul 2026", 200_000),
        SavingsHistory("Setoran Tabungan", "28 Jun 2026", 300_000),
        SavingsHistory("Setoran Tabungan", "15 Jun 2026", 150_000),
        SavingsHistory("Setoran Tabungan", "2 Jun 2026", 250_000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAllocationTitle.text = "Tiket Pesawat"
        binding.btnBack.setOnClickListener { finish() }

        setupSummaryCard()
        setupRecyclerView()

        binding.btnViewAllHistory.setOnClickListener {
            Toast.makeText(this, "Menampilkan seluruh riwayat", Toast.LENGTH_SHORT).show()
            // TODO: navigasi ke halaman full history / expand list
        }

    }

    private fun setupSummaryCard() {
        val totalTerkumpul = historyList.sumOf { it.amount }
        val persentase = if (targetNominal == 0L) 0
        else ((totalTerkumpul.toDouble() / targetNominal) * 100).toInt().coerceIn(0, 100)
        val sisa = (targetNominal - totalTerkumpul).coerceAtLeast(0)

        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalTerkumpul.text = "Rp ${rupiah.format(totalTerkumpul)}"
        binding.progressAllocation.progress = persentase
        binding.tvProgressCaption.text =
            "$persentase% dari Rp ${rupiah.format(targetNominal)} • Rp ${rupiah.format(sisa)} lagi"
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = SavingsHistoryAdapter(historyList)
    }

}