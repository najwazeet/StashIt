package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.Acara
import com.example.stashit.databinding.ItemAcaraBinding
import java.text.NumberFormat
import java.util.Locale

class AcaraAdapter(
    private val listAcara: List<Acara>,
    private val onItemClick: (Acara) -> Unit
) : RecyclerView.Adapter<AcaraAdapter.AcaraViewHolder>() {

    inner class AcaraViewHolder(val binding: ItemAcaraBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AcaraViewHolder {
        val binding = ItemAcaraBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AcaraViewHolder(binding)
    }

    override fun getItemCount(): Int = listAcara.size

    override fun onBindViewHolder(holder: AcaraViewHolder, position: Int) {
        val acara = listAcara[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))

        holder.binding.apply {
            tvNamaAcara.text = acara.namaAcara
            tvLokasi.text = "📍 ${acara.lokasi}"
            tvTanggal.text = acara.tanggal
            chipKategori.text = acara.kategori.uppercase()
            progressAcara.progress = acara.persentase
            tvPersen.text = "${acara.persentase}%"
            tvNominal.text = "Rp ${rupiah.format(acara.nominalTerkumpul)} / Rp ${rupiah.format(acara.targetNominal)}"

            root.setOnClickListener { onItemClick(acara) }
        }
    }
}