package com.example.stashit.data

import com.google.firebase.firestore.Exclude

data class Acara(
    @get:Exclude var idAcara: String = "",
    val id_user: String = "",
    val nama_acara: String = "",
    val tanggal: String = "",
    val lokasi: String = "",
    val target_nominal: Double = 0.0,
    val created_at: Long = System.currentTimeMillis()
) {
    @Exclude
    fun getNamaAcaraDisplay(): String = nama_acara
}