package com.example.androidtemplate.features.auth

import kotlinx.coroutines.delay

class DemoAuthRepository : AuthRepositoryContract {
  override suspend fun requestOtp(email: String): AuthResult {
    delay(200)
    return if (email.contains("rate", ignoreCase = true)) {
      AuthResult.RateLimited(30)
    } else {
      AuthResult.Success
    }
  }

  override suspend fun verifyOtp(email: String, code: String): AuthResult {
    delay(200)
    return if (code == "000000") {
      AuthResult.Failure("Invalid verification code")
    } else {
      AuthResult.Success
    }
  }

  override suspend fun logout(): AuthResult = AuthResult.Success
}
