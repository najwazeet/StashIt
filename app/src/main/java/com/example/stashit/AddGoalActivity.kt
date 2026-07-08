package com.example.stashit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.AllocationAdapter
import com.example.stashit.data.Allocation
import com.example.stashit.databinding.ActivityAddGoalBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGoalBinding
    private val allocationList = mutableListOf(
        Allocation("Tiket Pesawat", 1_500_000),
        Allocation("Penginapan", 800_000)
    )
    private lateinit var adapter: AllocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotalAllocation()

        binding.btnBack.setOnClickListener { finish() }

        binding.etTargetDate.setOnClickListener { showDatePicker() }

        binding.btnAddAllocation.setOnClickListener { handleAddAllocation() }

        binding.btnCreateGoal.setOnClickListener {
            val goalName = binding.etGoalName.text.toString()
            if (goalName.isBlank()) {
                Toast.makeText(this, "Nama goal tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // TODO: Simpan goal + allocationList ke Firestore
            Toast.makeText(this, "Goal \"$goalName\" berhasil dibuat!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = AllocationAdapter(allocationList) { position ->
            allocationList.removeAt(position)
            adapter.notifyDataSetChanged()
            updateTotalAllocation()
        }
        binding.rvAllocations.layoutManager = LinearLayoutManager(this)
        binding.rvAllocations.adapter = adapter
    }

    private fun handleAddAllocation() {
        val name = binding.etAllocationName.text.toString().trim()
        val amountText = binding.etAllocationAmount.text.toString().trim()

        if (name.isBlank() || amountText.isBlank()) {
            Toast.makeText(this, "Isi nama dan nominal alokasi", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toLongOrNull() ?: 0L
        allocationList.add(Allocation(name, amount))
        adapter.notifyItemInserted(allocationList.size - 1)
        updateTotalAllocation()

        binding.etAllocationName.text?.clear()
        binding.etAllocationAmount.text?.clear()
    }

    private fun updateTotalAllocation() {
        val total = allocationList.sumOf { it.amount }
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalAllocation.text = "Rp ${rupiah.format(total)}"
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                binding.etTargetDate.setText(sdf.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}