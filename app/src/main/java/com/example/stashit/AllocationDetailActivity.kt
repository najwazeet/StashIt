package com.example.stashit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.SavingsHistoryAdapter
import com.example.stashit.data.SavingsHistory
import com.example.stashit.repository.FirestoreRepository
import com.example.stashit.databinding.ActivityAllocationDetailBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AllocationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllocationDetailBinding
    private val repository = FirestoreRepository()

    private var idRincianBiaya: String = ""
    private var targetNominal: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idRincianBiaya = intent.getStringExtra("id_rincian_biaya") ?: ""
        if (idRincianBiaya.isEmpty()) {
            Toast.makeText(this, "Data alokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnViewAllHistory.setOnClickListener {
            Toast.makeText(this, "Menampilkan seluruh riwayat", Toast.LENGTH_SHORT).show()
            // TODO: navigasi ke halaman full history / expand list
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val rincian = repository.getRincianBiayaById(idRincianBiaya)
                if (rincian == null) {
                    Toast.makeText(this@AllocationDetailActivity, "Alokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                targetNominal = rincian.target_nominal.toLong()
                binding.tvAllocationTitle.text = rincian.nama_kebutuhan

                val historyEntries = repository.getHistoryList(idRincianBiaya)
                val historyList = historyEntries.map {
                    SavingsHistory(
                        title = "Setoran Tabungan",
                        date = it.tanggal,
                        amount = it.jumlah.toLong()
                    )
                }

                setupSummaryCard(rincian.nominal_terkumpul.toLong())
                setupRecyclerView(historyList)

            } catch (e: Exception) {
                Toast.makeText(
                    this@AllocationDetailActivity,
                    "Gagal memuat data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupSummaryCard(totalTerkumpul: Long) {
        val persentase = if (targetNominal == 0L) 0
        else ((totalTerkumpul.toDouble() / targetNominal) * 100).toInt().coerceIn(0, 100)
        val sisa = (targetNominal - totalTerkumpul).coerceAtLeast(0)

        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalTerkumpul.text = "Rp ${rupiah.format(totalTerkumpul)}"
        binding.progressAllocation.progress = persentase
        binding.tvProgressCaption.text =
            "$persentase% dari Rp ${rupiah.format(targetNominal)} • Rp ${rupiah.format(sisa)} lagi"
    }

    private fun setupRecyclerView(historyList: List<SavingsHistory>) {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = SavingsHistoryAdapter(historyList)
    }
}