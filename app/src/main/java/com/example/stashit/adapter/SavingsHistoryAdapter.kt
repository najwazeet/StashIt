package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.SavingsHistory
import com.example.stashit.databinding.ItemSavingsHistoryBinding
import java.text.NumberFormat
import java.util.Locale

class SavingsHistoryAdapter(
    private val items: List<SavingsHistory>
) : RecyclerView.Adapter<SavingsHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemSavingsHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HistoryViewHolder {
        val binding = ItemSavingsHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))

        holder.binding.apply {
            tvHistoryTitle.text = item.title
            tvHistoryDate.text = item.date
            tvHistoryAmount.text = "+Rp ${rupiah.format(item.amount)}"
        }
    }
}