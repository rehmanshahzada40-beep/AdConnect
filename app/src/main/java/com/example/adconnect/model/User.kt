package com.example.adconnect.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // "Advertiser" or "Publisher"
    val bio: String = ""
)