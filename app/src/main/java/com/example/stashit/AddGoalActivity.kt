package com.example.stashit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.AllocationAdapter
import com.example.stashit.data.Acara
import com.example.stashit.data.Allocation
import com.example.stashit.data.RincianBiaya
import com.example.stashit.databinding.ActivityAddGoalBinding
import com.example.stashit.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGoalBinding
    private val allocationList = mutableListOf<Allocation>()
    private lateinit var adapter: AllocationAdapter
    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotalAllocation()

        binding.btnBack.setOnClickListener { finish() }

        binding.etTargetDate.setOnClickListener { showDatePicker() }

        binding.btnAddAllocation.setOnClickListener { handleAddAllocation() }

        binding.btnCreateGoal.setOnClickListener { handleCreateGoal() }
    }

    private fun handleCreateGoal() {
        val goalName = binding.etGoalName.text.toString().trim()
        val lokasi = binding.etLokasi.text.toString().trim()
        val targetDate = binding.etTargetDate.text.toString().trim()
        val targetAmount = allocationList.sumOf { it.amount }

        if (goalName.isEmpty()) {
            Toast.makeText(this, "Nama goal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (lokasi.isEmpty()) {
            Toast.makeText(this, "Lokasi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (targetDate.isEmpty()) {
            Toast.makeText(this, "Pilih target tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (allocationList.isEmpty()) {
            Toast.makeText(this, "Tambahkan minimal satu alokasi", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId.isNullOrEmpty()) {
            Toast.makeText(this, "Sesi login tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnCreateGoal.isEnabled = false
        binding.btnCreateGoal.text = "Menyimpan..."

        lifecycleScope.launch {
            try {
                val acara = Acara(
                    id_user = currentUserId,
                    nama_acara = goalName,
                    tanggal = targetDate,
                    lokasi = lokasi,
                    target_nominal = targetAmount.toDouble()
                )
                val idAcara = repository.addAcara(acara)

                allocationList.forEach { alokasi ->
                    val rincian = RincianBiaya(
                        id_acara = idAcara,
                        nama_kebutuhan = alokasi.name,
                        target_nominal = alokasi.amount.toDouble(),
                        nominal_terkumpul = 0.0
                    )
                    repository.addRincianBiaya(rincian)
                }

                Toast.makeText(
                    this@AddGoalActivity,
                    "Goal \"$goalName\" berhasil dibuat!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                binding.btnCreateGoal.isEnabled = true
                binding.btnCreateGoal.text = "Create Goal & Start Saving"
                Toast.makeText(
                    this@AddGoalActivity,
                    "Gagal menyimpan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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