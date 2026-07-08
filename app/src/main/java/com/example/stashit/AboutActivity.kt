package com.example.stashit

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stashit.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMenuItems()
        setupListeners()
    }

    private fun setupMenuItems() {
        binding.itemRateApp.tvMenuLabel.text = "Rate This App"
        binding.itemRateApp.ivMenuIcon.setImageResource(android.R.drawable.btn_star_big_on)
        binding.itemRateApp.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemShareApp.tvMenuLabel.text = "Share This App"
        binding.itemShareApp.ivMenuIcon.setImageResource(android.R.drawable.ic_menu_share)
        binding.itemShareApp.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemDeveloper.tvMenuLabel.text = "Developer"
        binding.itemDeveloper.ivMenuIcon.setImageResource(R.drawable.ic_person)
        binding.itemDeveloper.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemContact.tvMenuLabel.text = "Contact Support"
        binding.itemContact.ivMenuIcon.setImageResource(android.R.drawable.ic_dialog_email)
        binding.itemContact.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemPrivacyPolicy.tvMenuLabel.text = "Privacy Policy"
        binding.itemPrivacyPolicy.ivMenuIcon.setImageResource(R.drawable.ic_lock)
        binding.itemPrivacyPolicy.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))

        binding.itemTerms.tvMenuLabel.text = "Terms of Service"
        binding.itemTerms.ivMenuIcon.setImageResource(android.R.drawable.ic_menu_info_details)
        binding.itemTerms.ivMenuIcon.setColorFilter(Color.parseColor("#616161"))
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.itemRateApp.root.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        binding.itemShareApp.root.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Coba aplikasi Stash.it: https://play.google.com/store/apps/details?id=$packageName")
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        binding.itemDeveloper.root.setOnClickListener {
            Toast.makeText(this, "Dikembangkan oleh Najwa & Titi", Toast.LENGTH_SHORT).show()
        }

        binding.itemContact.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:stashit.support@gmail.com")
            }
            startActivity(intent)
        }

        binding.itemPrivacyPolicy.root.setOnClickListener {
            Toast.makeText(this, "Privacy Policy belum tersedia", Toast.LENGTH_SHORT).show()
        }

        binding.itemTerms.root.setOnClickListener {
            Toast.makeText(this, "Terms of Service belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }
}