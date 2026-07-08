package com.example.stashit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.DetailAllocationAdapter
import com.example.stashit.data.DetailAllocation
import com.example.stashit.databinding.ActivityDetailEventBinding
import java.text.NumberFormat
import java.util.Locale
import android.content.Intent
import android.view.ViewGroup

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding

    private val allocationList = listOf(
        DetailAllocation("Tiket Pesawat", 1_500_000, 900_000),
        DetailAllocation("Penginapan", 1_000_000, 620_000),
        DetailAllocation("Konsumsi", 500_000, 200_000),
        DetailAllocation("Transportasi Lokal", 500_000, 0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        setupHeaderSummary()
        setupBottomNav()

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.fabAddAllocation.setOnClickListener {
            val intent = Intent(this, AddAllocationActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNav.post {
            val params = binding.fabAddAllocation.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = binding.bottomNav.height + 24
            binding.fabAddAllocation.layoutParams = params
        }
    }

    private fun setupRecyclerView() {
        val adapter = DetailAllocationAdapter(
            items = allocationList,
            onAddFundClick = { position ->
                val item = allocationList[position]
                AddFundBottomSheet(item.title) { amount, dateLabel ->
                    Toast.makeText(this, "Rp $amount ditambahkan pada $dateLabel", Toast.LENGTH_SHORT).show()
                }.show(supportFragmentManager, "AddFundBottomSheet")
            },
            onItemClick = { position ->
                val intent = Intent(this, AllocationDetailActivity::class.java)
                startActivity(intent)
            }
        )
        binding.rvAllocations.layoutManager = LinearLayoutManager(this)
        binding.rvAllocations.adapter = adapter
    }

    private fun setupHeaderSummary() {
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        val totalTarget = allocationList.sumOf { it.target }
        val totalTerkumpul = allocationList.sumOf { it.terkumpul }
        val persentase = if (totalTarget == 0L) 0
        else ((totalTerkumpul.toDouble() / totalTarget) * 100).toInt().coerceIn(0, 100)
        val sisa = (totalTarget - totalTerkumpul).coerceAtLeast(0)

        binding.tvNominalTerkumpul.text = "Rp ${rupiah.format(totalTerkumpul)}"
        binding.tvNominalTarget.text = "Rp ${rupiah.format(totalTarget)}"
        binding.progressEvent.progress = persentase
        binding.tvProgressCaption.text =
            "$persentase% tercapai • Rp ${rupiah.format(sisa)} lagi menuju target"
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_START_TAB, item.itemId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
            true
        }
    }
}