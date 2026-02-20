package com.example.androidtemplate.features.auth

import kotlinx.coroutines.delay

class DemoAuthRepository : AuthRepositoryContract {
  override fun buildOAuthAuthorizeUrl(provider: OAuthProvider): String? {
    return "androidtemplate://auth/callback#access_token=demo-token-${provider.providerKey}"
  }

  override suspend fun completeOAuthCallback(callbackUri: String): AuthResult {
    return if (callbackUri.contains("error=")) {
      AuthResult.Failure("OAuth failed")
    } else {
      AuthResult.Success
    }
  }

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
