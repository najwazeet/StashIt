package com.example.stashit

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.DetailAllocationAdapter
import com.example.stashit.data.Acara
import com.example.stashit.data.DetailAllocation
import com.example.stashit.data.SavingsHistoryEntry
import com.example.stashit.databinding.ActivityDetailEventBinding
import com.example.stashit.repository.FirestoreRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var adapter: DetailAllocationAdapter
    private val repository = FirestoreRepository()

    private val allocationList = mutableListOf<DetailAllocation>()
    private var idAcara: String = ""
    private var currentAcara: Acara? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idAcara = intent.getStringExtra("id_acara") ?: ""
        if (idAcara.isEmpty()) {
            Toast.makeText(this, "Data acara tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        setupBottomNav()

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(this, NotificationUiActivity::class.java))
        }

        binding.btnEditEvent.setOnClickListener { showEditAcaraDialog() }
        binding.btnDeleteEvent.setOnClickListener { confirmDeleteAcara() }

        binding.fabAddAllocation.setOnClickListener {
            val intent = Intent(this, AddAllocationActivity::class.java)
            intent.putExtra("id_acara", idAcara)
            startActivity(intent)
        }

        binding.bottomNav.post {
            val params = binding.fabAddAllocation.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = binding.bottomNav.height + 24
            binding.fabAddAllocation.layoutParams = params
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = DetailAllocationAdapter(
            items = allocationList,
            onAddFundClick = { position ->
                val item = allocationList[position]
                AddFundBottomSheet(item.title) { amount, dateLabel ->
                    tambahDana(item, amount, dateLabel)
                }.show(supportFragmentManager, "AddFundBottomSheet")
            },
            onItemClick = { position ->
                val item = allocationList[position]
                val intent = Intent(this, AllocationDetailActivity::class.java)
                intent.putExtra("id_rincian_biaya", item.idRincianBiaya)
                startActivity(intent)
            },
            onEditClick = { position ->
                showEditAllocationDialog(allocationList[position])
            },
            onDeleteClick = { position ->
                confirmDeleteAllocation(allocationList[position])
            }
        )
        binding.rvAllocations.layoutManager = LinearLayoutManager(this)
        binding.rvAllocations.adapter = adapter
    }

    // ===== EDIT & DELETE ACARA =====

    private fun showEditAcaraDialog() {
        val acara = currentAcara ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_acara, null)
        val etNama = dialogView.findViewById<android.widget.EditText>(R.id.etEditNamaAcara)
        val etTanggal = dialogView.findViewById<android.widget.EditText>(R.id.etEditTanggal)
        val etLokasi = dialogView.findViewById<android.widget.EditText>(R.id.etEditLokasi)

        etNama.setText(acara.nama_acara)
        etTanggal.setText(acara.tanggal)
        etLokasi.setText(acara.lokasi)

        etTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                    etTanggal.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Acara")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = etNama.text.toString().trim()
                val tanggalBaru = etTanggal.text.toString().trim()
                val lokasiBaru = etLokasi.text.toString().trim()

                if (namaBaru.isEmpty() || tanggalBaru.isEmpty()) {
                    Toast.makeText(this, "Nama dan tanggal tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        repository.updateAcara(idAcara, namaBaru, tanggalBaru, lokasiBaru)
                        Toast.makeText(this@DetailEventActivity, "Acara berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadData()
                    } catch (e: Exception) {
                        Toast.makeText(this@DetailEventActivity, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDeleteAcara() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Acara")
            .setMessage("Yakin mau hapus acara ini? Semua alokasi & histori tabungan di dalamnya juga akan terhapus permanen.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.deleteAcara(idAcara)
                        Toast.makeText(this@DetailEventActivity, "Acara berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@DetailEventActivity, "Gagal hapus: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ===== EDIT & DELETE ALLOCATION =====

    private fun showEditAllocationDialog(item: DetailAllocation) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_allocation, null)
        val etNama = dialogView.findViewById<android.widget.EditText>(R.id.etEditAllocationName)
        val etTarget = dialogView.findViewById<android.widget.EditText>(R.id.etEditAllocationTarget)

        etNama.setText(item.title)
        etTarget.setText(item.target.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Alokasi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = etNama.text.toString().trim()
                val targetBaru = etTarget.text.toString().trim().toDoubleOrNull()

                if (namaBaru.isEmpty() || targetBaru == null) {
                    Toast.makeText(this, "Isi nama dan target dengan benar", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        repository.updateRincianBiaya(item.idRincianBiaya, namaBaru, targetBaru)
                        Toast.makeText(this@DetailEventActivity, "Alokasi berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadData()
                    } catch (e: Exception) {
                        Toast.makeText(this@DetailEventActivity, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDeleteAllocation(item: DetailAllocation) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Alokasi")
            .setMessage("Yakin mau hapus alokasi \"${item.title}\"? Histori tabungannya juga akan ikut terhapus.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.deleteRincianBiaya(item.idRincianBiaya)
                        Toast.makeText(this@DetailEventActivity, "Alokasi berhasil dihapus", Toast.LENGTH_SHORT).show()
                        loadData()
                    } catch (e: Exception) {
                        Toast.makeText(this@DetailEventActivity, "Gagal hapus: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tambahDana(item: DetailAllocation, amount: Long, dateLabel: String) {
        lifecycleScope.launch {
            try {
                val nominalBaru = item.terkumpul + amount
                repository.updateNominalTerkumpul(item.idRincianBiaya, nominalBaru.toDouble())
                repository.addHistoryEntry(
                    item.idRincianBiaya,
                    SavingsHistoryEntry(jumlah = amount.toDouble(), tanggal = dateLabel)
                )
                Toast.makeText(
                    this@DetailEventActivity,
                    "Rp $amount ditambahkan pada $dateLabel",
                    Toast.LENGTH_SHORT
                ).show()
                loadData()
            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailEventActivity,
                    "Gagal menyimpan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            try {
                val acara = repository.getAcaraById(idAcara)
                if (acara == null) {
                    Toast.makeText(this@DetailEventActivity, "Acara tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                currentAcara = acara

                binding.tvEventName.text = acara.nama_acara
                binding.tvEventLocation.text = "📍 ${acara.lokasi} • ${acara.tanggal}"

                val rincianList = repository.getRincianBiayaList(idAcara)
                val detailList = rincianList.map {
                    DetailAllocation(
                        idRincianBiaya = it.idRincianBiaya,
                        title = it.nama_kebutuhan,
                        target = it.target_nominal.toLong(),
                        terkumpul = it.nominal_terkumpul.toLong()
                    )
                }
                adapter.updateData(detailList)

                setupHeaderSummary(detailList)

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailEventActivity,
                    "Gagal memuat data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupHeaderSummary(list: List<DetailAllocation>) {
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        val totalTarget = list.sumOf { it.target }
        val totalTerkumpul = list.sumOf { it.terkumpul }
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