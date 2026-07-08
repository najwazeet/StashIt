package com.example.stashit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.adapter.NotificationsAdapter
import com.example.stashit.data.NotificationItem
import com.example.stashit.databinding.ActivityNotificationBinding

class NotificationUiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val dummyData = listOf(
            NotificationItem(
                title = "Waktunya isi tabungan!",
                description = "Target Bali Trip kamu masih Rp 1.500.000 lagi. Yuk sisihkan sedikit hari ini biar makin dekat ke tujuan!",
                time = "10m",
                iconRes = R.drawable.ic_savings,
                iconBgRes = R.drawable.bg_icon_circle_pink
            ),
            NotificationItem(
                title = "Progress kamu naik!",
                description = "Selamat, tabungan Konser Coldplay sudah mencapai 90%. Sedikit lagi target tercapai!",
                time = "3j",
                iconRes = R.drawable.ic_trending_up,
                iconBgRes = R.drawable.bg_icon_circle_mint
            ),
            NotificationItem(
                title = "Jangan lupa nabung hari ini",
                description = "Sudah 3 hari kamu belum menambah tabungan untuk Wisuda Sahabat. Yuk mulai lagi sedikit demi sedikit!",
                time = "1h",
                iconRes = R.drawable.ic_notification,
                iconBgRes = R.drawable.bg_icon_circle_lavender
            ),
            NotificationItem(
                title = "Target baru saja dibuat",
                description = "Goal 'Wisuda Sahabat' berhasil ditambahkan. Ayo mulai menabung dari sekarang!",
                time = "2h",
                iconRes = R.drawable.ic_flag,
                iconBgRes = R.drawable.bg_icon_circle_pink
            )
        )

        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = NotificationsAdapter(dummyData)
    }
}