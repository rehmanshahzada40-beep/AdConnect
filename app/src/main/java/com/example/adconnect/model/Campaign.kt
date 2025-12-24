package com.example.adconnect.model

import com.google.firebase.Timestamp

data class Campaign(
    val campaignId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val createdByUserId: String = "",
    val timestamp: Timestamp? = null
)