package com.example.assessment_2.data.repository

import com.example.assessment_2.data.Result
import com.example.assessment_2.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<UserEntity>
    suspend fun signIn(email: String, password: String): Result<UserEntity>
    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Boolean>
    fun observeCurrentUser(): Flow<UserEntity?>
    suspend fun logout()
}