package com.example.stashit

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stashit.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val prefsName = "notification_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        binding.switchReminder.isChecked = prefs.getBoolean("reminder_tabungan", true)
        binding.switchProgress.isChecked = prefs.getBoolean("update_progress", true)
        binding.switchH3.isChecked = prefs.getBoolean("h3_sebelum_acara", true)
        binding.switchEmail.isChecked = prefs.getBoolean("notif_email", false)
        binding.switchSound.isChecked = prefs.getBoolean("suara_notif", true)
        binding.switchVibration.isChecked = prefs.getBoolean("getar_notif", true)

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reminder_tabungan", isChecked).apply()
        }
        binding.switchProgress.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("update_progress", isChecked).apply()
        }
        binding.switchH3.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("h3_sebelum_acara", isChecked).apply()
        }
        binding.switchEmail.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notif_email", isChecked).apply()
        }
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("suara_notif", isChecked).apply()
        }
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("getar_notif", isChecked).apply()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}