package com.example.stashit.data

data class DetailAllocation(
    val idRincianBiaya: String,
    val title: String,
    val target: Long,
    val terkumpul: Long
) {
    val persentase: Int
        get() = if (target == 0L) 0
        else ((terkumpul.toDouble() / target) * 100).toInt().coerceIn(0, 100)
}