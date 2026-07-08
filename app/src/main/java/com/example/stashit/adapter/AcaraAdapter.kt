package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.Acara
import com.example.stashit.data.AcaraUiModel
import com.example.stashit.databinding.ItemAcaraBinding
import java.text.NumberFormat
import java.util.Locale

class AcaraAdapter(
    private val listAcara: List<AcaraUiModel>,
    private val onItemClick: (Acara) -> Unit
) : RecyclerView.Adapter<AcaraAdapter.AcaraViewHolder>() {

    inner class AcaraViewHolder(val binding: ItemAcaraBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcaraViewHolder {
        val binding = ItemAcaraBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AcaraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AcaraViewHolder, position: Int) {
        val item = listAcara[position]
        val acara = item.acara
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))

        val persentase = if (item.totalTarget == 0.0) 0
        else ((item.totalTerkumpul / item.totalTarget) * 100).toInt().coerceIn(0, 100)

        holder.binding.apply {
            tvNamaAcara.text = acara.nama_acara
            tvLokasi.text = "📍 ${acara.lokasi}"
            tvTanggal.text = acara.tanggal
            progressAcara.progress = persentase
            tvPersen.text = "$persentase%"
            tvNominal.text = "Rp ${rupiah.format(item.totalTerkumpul.toLong())} / Rp ${rupiah.format(item.totalTarget.toLong())}"

            root.setOnClickListener { onItemClick(acara) }
        }
    }

    override fun getItemCount() = listAcara.size
}