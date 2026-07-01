package com.example.stashit.data

data class Acara(
    val namaAcara: String,
    val lokasi: String,
    val tanggal: String,
    val kategori: String,
    val targetNominal: Long,
    val nominalTerkumpul: Long
) {
    val persentase: Int
        get() = if (targetNominal == 0L) 0
        else ((nominalTerkumpul.toDouble() / targetNominal) * 100).toInt().coerceIn(0, 100)
}