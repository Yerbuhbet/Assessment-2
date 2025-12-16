package com.example.assessment_2.data.remote

import com.example.assessment_2.data.remote.dto.AuthResponse
import com.example.assessment_2.data.remote.dto.SignInRequest
import com.example.assessment_2.data.remote.dto.SignUpRequest
import com.example.assessment_2.data.remote.dto.ResetPasswordRequest
import com.example.assessment_2.data.remote.dto.ResetPasswordResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // ReqRes: https://reqres.in/api/register (POST) -> { id, token }
    @POST("api/register")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    // ReqRes: https://reqres.in/api/login (POST) -> { token }
    @POST("api/login")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    // No direct endpoint on reqres for password reset; we simulate success
    @POST("api/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse
}