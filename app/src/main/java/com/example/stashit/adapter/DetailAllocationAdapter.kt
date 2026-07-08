package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.DetailAllocation
import com.example.stashit.databinding.ItemDetailAllocationBinding
import java.text.NumberFormat
import java.util.Locale

class DetailAllocationAdapter(
    private val items: List<DetailAllocation>,
    private val onAddFundClick: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DetailAllocationAdapter.DetailAllocationViewHolder>() {

    inner class DetailAllocationViewHolder(val binding: ItemDetailAllocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DetailAllocationViewHolder {
        val binding = ItemDetailAllocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DetailAllocationViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DetailAllocationViewHolder, position: Int) {
        val item = items[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))

        holder.binding.apply {
            tvAllocationTitle.text = item.title
            tvAllocationPercent.text = "${item.persentase}%"
            tvAllocationNominal.text =
                "Rp ${rupiah.format(item.terkumpul)} / Rp ${rupiah.format(item.target)}"
            progressAllocation.progress = item.persentase

            btnAddFund.setOnClickListener {
                onAddFundClick(holder.adapterPosition)
            }
            root.setOnClickListener {
                onItemClick(holder.adapterPosition)
            }
        }
    }
}