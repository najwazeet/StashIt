package com.example.stashit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.AcaraAdapter
import com.example.stashit.data.Acara
import com.example.stashit.databinding.ActivityDaftarAcaraBinding
import java.text.NumberFormat
import java.util.Locale

class DaftarAcaraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarAcaraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarAcaraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummyAcara = listOf(
            Acara(
                namaAcara = "Trip ke Bandung",
                lokasi = "Bandung, Jawa Barat",
                tanggal = "12 Agu 2026",
                kategori = "Liburan",
                targetNominal = 2_000_000,
                nominalTerkumpul = 800_000
            ),
            Acara(
                namaAcara = "Konser Coldplay",
                lokasi = "Jakarta",
                tanggal = "3 Sep 2026",
                kategori = "Konser",
                targetNominal = 3_500_000,
                nominalTerkumpul = 3_150_000
            ),
            Acara(
                namaAcara = "Wisuda Sahabat",
                lokasi = "Denpasar, Bali",
                tanggal = "20 Jul 2026",
                kategori = "Acara",
                targetNominal = 500_000,
                nominalTerkumpul = 120_000
            )
        )

        // Setup RecyclerView
        binding.rvAcara.layoutManager = LinearLayoutManager(this)
        binding.rvAcara.adapter = AcaraAdapter(dummyAcara) { acara ->
            // TODO: navigasi ke Detail Acara
        }

        // Hitung total tabungan keseluruhan (header)
        val totalTerkumpul = dummyAcara.sumOf { it.nominalTerkumpul }
        val totalTarget = dummyAcara.sumOf { it.targetNominal }
        val persentaseTotal = if (totalTarget == 0L) 0
        else ((totalTerkumpul.toDouble() / totalTarget) * 100).toInt().coerceIn(0, 100)
        val sisa = (totalTarget - totalTerkumpul).coerceAtLeast(0)

        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        binding.tvTotalTabungan.text = "Rp ${rupiah.format(totalTerkumpul)}"
        binding.progressTotal.progress = persentaseTotal
        binding.tvProgressCaption.text =
            "$persentaseTotal% dari target • Rp ${rupiah.format(sisa)} lagi"
    }
}