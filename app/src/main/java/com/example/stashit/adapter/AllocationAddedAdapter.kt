package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.AllocationItem
import com.example.stashit.databinding.ItemAllocationAddedBinding
import java.text.NumberFormat
import java.util.Locale

class AllocationAddedAdapter(
    private val items: MutableList<AllocationItem>
) : RecyclerView.Adapter<AllocationAddedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAllocationAddedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAllocationAddedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        holder.binding.tvItemName.text = item.name
        holder.binding.tvItemAmount.text = "Rp ${rupiah.format(item.amount)}"
    }

    override fun getItemCount() = items.size

    fun addItem(item: AllocationItem) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun getItems(): List<AllocationItem> = items.toList()
}