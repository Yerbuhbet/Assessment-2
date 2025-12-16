package com.example.assessment_2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val authStatus: String // e.g., LOGGED_IN
)
