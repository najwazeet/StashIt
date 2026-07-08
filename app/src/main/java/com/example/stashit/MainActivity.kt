package com.example.stashit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stashit.databinding.ActivityMainBinding
import com.example.stashit.fragment.HomeFragment
import com.example.stashit.fragment.ProfileFragment
import com.example.stashit.fragment.StatsFragment
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startTab = intent.getIntExtra(EXTRA_START_TAB, R.id.nav_home)

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = startTab
            replaceFragment(fragmentForTab(startTab))
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            replaceFragment(fragmentForTab(item.itemId))
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tabId = intent.getIntExtra(EXTRA_START_TAB, R.id.nav_home)
        binding.bottomNav.selectedItemId = tabId
    }

    private fun fragmentForTab(tabId: Int): Fragment {
        return when (tabId) {
            R.id.nav_stats -> StatsFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    companion object {
        const val EXTRA_START_TAB = "extra_start_tab"
    }
}