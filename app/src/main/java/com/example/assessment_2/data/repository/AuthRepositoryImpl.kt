package com.example.assessment_2.data.repository

import com.example.assessment_2.data.Result
import com.example.assessment_2.data.local.UserDao
import com.example.assessment_2.data.local.UserEntity
import com.example.assessment_2.data.remote.AuthApiService
import com.example.assessment_2.data.remote.dto.ResetPasswordRequest
import com.example.assessment_2.data.remote.dto.SignInRequest
import com.example.assessment_2.data.remote.dto.SignUpRequest
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val userDao: UserDao,
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<UserEntity> {
        return safeCall {
            val response = api.signUp(SignUpRequest(email = email, password = password))
            val userId = response.id ?: response.token ?: ""
            if (userId.isBlank()) throw IllegalStateException("Invalid sign up response")
            val entity = UserEntity(userId = userId, email = email, authStatus = "LOGGED_IN")
            userDao.upsert(entity)
            Result.Success(entity)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserEntity> {
        return safeCall {
            val response = api.signIn(SignInRequest(email = email, password = password))
            val userId = response.id ?: response.token ?: ""
            if (userId.isBlank()) throw IllegalStateException("Invalid sign in response")
            val entity = UserEntity(userId = userId, email = email, authStatus = "LOGGED_IN")
            userDao.upsert(entity)
            Result.Success(entity)
        }
    }

    override suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Boolean> {
        // Attempt network call first. If the backend endpoint doesn't exist, this will return an error.
        return safeCall {
            val resp = api.resetPassword(ResetPasswordRequest(email = email, otp = otp, newPassword = newPassword))
            Result.Success(resp.success)
        }
    }

    override fun observeCurrentUser(): Flow<UserEntity?> = userDao.observeCurrentUser()

    override suspend fun logout() {
        userDao.clear()
    }

    // Helper for mapping exceptions to Result.Error
    private inline fun <T> safeCall(block: () -> Result<T>): Result<T> =
        try {
            block()
        } catch (t: Throwable) {
            Result.Error(message = t.message ?: "Unknown error", throwable = t)
        }
}
