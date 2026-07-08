package com.example.stashit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.databinding.ActivityLoginActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LoginActivityHistory : AppCompatActivity() {

    private lateinit var binding: ActivityLoginActivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.ivBack.setOnClickListener { finish() }
        loadSessions()
    }

    private fun loadSessions() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).collection("sessions")
            .orderBy("loginTime", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.documents.map { doc ->
                    SessionModel(
                        sessionId = doc.id,
                        deviceName = doc.getString("deviceName") ?: "Unknown device",
                        loginTime = doc.getTimestamp("loginTime"),
                        isCurrentDevice = doc.id == android.provider.Settings.Secure.getString(
                            contentResolver, android.provider.Settings.Secure.ANDROID_ID
                        )
                    )
                }

                if (sessions.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rvSessions.layoutManager = LinearLayoutManager(this)
                    binding.rvSessions.adapter = SessionAdapter(sessions)
                }
            }
    }
}