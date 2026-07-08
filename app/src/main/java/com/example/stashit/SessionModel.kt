package com.example.stashit

data class SessionModel(
    val sessionId: String = "",
    val deviceName: String = "",
    val loginTime: com.google.firebase.Timestamp? = null,
    val isCurrentDevice: Boolean = false
)