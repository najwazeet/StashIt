package com.example.stashit.data

import com.google.firebase.firestore.Exclude

data class RincianBiaya(
    @get:Exclude var idRincianBiaya: String = "",
    val id_acara: String = "",
    val nama_kebutuhan: String = "",
    val target_nominal: Double = 0.0,
    val nominal_terkumpul: Double = 0.0,
    val created_at: Long = System.currentTimeMillis()
)