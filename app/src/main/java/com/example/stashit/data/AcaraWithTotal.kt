package com.example.stashit.data

data class AcaraWithTotal(
    val acara: Acara,
    val totalTarget: Long,
    val totalTerkumpul: Long
) {
    val isCompleted: Boolean
        get() = totalTarget > 0 && totalTerkumpul >= totalTarget
}