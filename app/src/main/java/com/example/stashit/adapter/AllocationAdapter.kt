package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.Allocation
import com.example.stashit.databinding.ItemAllocationBinding
import java.text.NumberFormat
import java.util.Locale

class AllocationAdapter(
    private val items: MutableList<Allocation>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AllocationAdapter.AllocationViewHolder>() {

    inner class AllocationViewHolder(val binding: ItemAllocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AllocationViewHolder {
        val binding = ItemAllocationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AllocationViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AllocationViewHolder, position: Int) {
        val item = items[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))

        holder.binding.apply {
            tvAllocationName.text = item.name
            tvAllocationAmount.text = "Rp ${rupiah.format(item.amount)}"
            btnDeleteAllocation.setOnClickListener {
                onDeleteClick(holder.adapterPosition)
            }
        }
    }
}