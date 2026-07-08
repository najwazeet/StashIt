package com.example.stashit.data

data class EventCompleted(
    val title: String,
    val date: String,
    val terkumpul: Long,
    val target: Long,
    val iconRes: Int,
    val iconBgRes: Int
)