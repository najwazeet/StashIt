package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.EventCompleted
import com.example.stashit.databinding.ItemEventCompletedBinding
import java.text.NumberFormat
import java.util.Locale

class EventsCompletedAdapter(
    private val items: List<EventCompleted>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EventsCompletedAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventCompletedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventCompletedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val rupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
        val persentase = if (item.target == 0L) 0
        else ((item.terkumpul.toDouble() / item.target) * 100).toInt().coerceIn(0, 100)

        with(holder.binding) {
            tvTitle.text = item.title
            tvDate.text = item.date
            tvNominal.text = "Rp ${rupiah.format(item.terkumpul)} / ${rupiah.format(item.target)}"
            tvPercentage.text = "$persentase%"
            progressEvent.progress = persentase
            ivCategoryIcon.setImageResource(item.iconRes)
            iconContainer.background = holder.itemView.context
                .getDrawable(item.iconBgRes)

            root.setOnClickListener { onItemClick(position) }
        }
    }

    override fun getItemCount() = items.size
}