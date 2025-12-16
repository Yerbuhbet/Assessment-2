package com.example.assessment_2.data.remote.dto

import com.squareup.moshi.Json

// ReqRes.in request/response models

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    // ReqRes returns an id for sign up, a token for login; we'll normalize in repository
    val id: String? = null,
    val token: String? = null,
    val email: String? = null
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    @Json(name = "new_password") val newPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String? = null
)
