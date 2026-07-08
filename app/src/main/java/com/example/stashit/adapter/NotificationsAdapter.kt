package com.example.stashit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.data.NotificationItem
import com.example.stashit.databinding.ItemNotificationBinding

class NotificationsAdapter(
    private val items: List<NotificationItem>
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvNotifTitle.text = item.title
            tvNotifDesc.text = item.description
            tvTime.text = item.time
            ivNotifIcon.setImageResource(item.iconRes)
            iconContainer.background = holder.itemView.context.getDrawable(item.iconBgRes)
        }
    }

    override fun getItemCount() = items.size
}