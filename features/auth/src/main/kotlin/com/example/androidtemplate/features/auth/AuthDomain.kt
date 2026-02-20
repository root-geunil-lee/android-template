package com.example.androidtemplate.features.auth

sealed interface AuthResult {
  data object Success : AuthResult
  data class RateLimited(val retryAfterSeconds: Int) : AuthResult
  data class Failure(val message: String) : AuthResult
}

interface AuthRepositoryContract {
  suspend fun requestOtp(email: String): AuthResult
  suspend fun verifyOtp(email: String, code: String): AuthResult
  suspend fun logout(): AuthResult
}

sealed interface OtpFlowState {
  data object Idle : OtpFlowState
  data object SendingCode : OtpFlowState
  data class SentCode(val email: String, val cooldownSeconds: Int) : OtpFlowState
  data object VerifyingCode : OtpFlowState
  data object VerifiedSuccess : OtpFlowState
  data class RateLimited(val retryAfterSeconds: Int) : OtpFlowState
  data class Error(val message: String) : OtpFlowState
}

class OtpAuthUseCase(
  private val repository: AuthRepositoryContract,
  private val defaultCooldownSeconds: Int = 30,
) {
  var state: OtpFlowState = OtpFlowState.Idle
    private set

  suspend fun sendCode(email: String): OtpFlowState {
    if (!email.isValidEmail()) {
      state = OtpFlowState.Error("Please enter a valid email")
      return state
    }

    state = OtpFlowState.SendingCode
    state = when (val result = repository.requestOtp(email)) {
      AuthResult.Success -> OtpFlowState.SentCode(email = email, cooldownSeconds = defaultCooldownSeconds)
      is AuthResult.RateLimited -> OtpFlowState.RateLimited(result.retryAfterSeconds)
      is AuthResult.Failure -> OtpFlowState.Error(result.message)
    }
    return state
  }

  suspend fun verifyCode(email: String, code: String): OtpFlowState {
    if (code.length != 6 || code.any { !it.isDigit() }) {
      state = OtpFlowState.Error("Invalid verification code")
      return state
    }

    state = OtpFlowState.VerifyingCode
    state = when (val result = repository.verifyOtp(email = email, code = code)) {
      AuthResult.Success -> OtpFlowState.VerifiedSuccess
      is AuthResult.RateLimited -> OtpFlowState.RateLimited(result.retryAfterSeconds)
      is AuthResult.Failure -> OtpFlowState.Error(result.message)
    }
    return state
  }
}

private fun String.isValidEmail(): Boolean = contains("@") && contains(".")
