package com.example.stashit.data

data class SavingsHistoryEntry(
    var idHistory: String = "",
    val jumlah: Double = 0.0,
    val tanggal: String = "",
    val created_at: Long = System.currentTimeMillis()
)