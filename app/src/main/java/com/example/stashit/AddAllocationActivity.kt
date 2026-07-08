package com.example.stashit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.AllocationAddedAdapter
import com.example.stashit.data.AllocationItem
import com.example.stashit.databinding.ActivityAddAllocationBinding

class AddAllocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAllocationBinding
    private lateinit var adapter: AllocationAddedAdapter

    private val dummyAllocations = mutableListOf(
        AllocationItem("Tiket Pesawat", 1_480_000),
        AllocationItem("Penginapan", 1_200_000),
        AllocationItem("Konsumsi", 900_000),
        AllocationItem("Transportasi", 650_000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAllocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AllocationAddedAdapter(dummyAllocations)
        binding.rvAllocationList.layoutManager = LinearLayoutManager(this)
        binding.rvAllocationList.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.fabAddField.setOnClickListener {
            val name = binding.etAllocationName.text.toString().trim()
            val targetText = binding.etTargetAmount.text.toString().trim()

            if (name.isEmpty() || targetText.isEmpty()) {
                Toast.makeText(this, "Isi nama dan target dulu ya", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val target = targetText.toLongOrNull()
            if (target == null) {
                Toast.makeText(this, "Target amount harus angka", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            adapter.addItem(AllocationItem(name, target))
            binding.etAllocationName.text.clear()
            binding.etTargetAmount.text.clear()
            binding.rvAllocationList.scrollToPosition(0)
        }

        binding.btnAddAllocation.setOnClickListener {
            Toast.makeText(this, "Allocation berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}