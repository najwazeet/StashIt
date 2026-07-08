package com.example.stashit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashit.databinding.ItemSessionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SessionAdapter(private val sessions: List<SessionModel>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    inner class SessionViewHolder(val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.binding.tvDeviceName.text = session.deviceName

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        holder.binding.tvSessionTime.text = session.loginTime?.toDate()?.let { sdf.format(it) } ?: "-"

        holder.binding.tvCurrentBadge.visibility = if (session.isCurrentDevice) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = sessions.size
}