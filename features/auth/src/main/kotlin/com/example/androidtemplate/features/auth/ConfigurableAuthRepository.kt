package com.example.androidtemplate.features.auth

class ConfigurableAuthRepository(
  authBaseUrl: String,
  private val fallbackRepository: AuthRepositoryContract,
  private val remoteRepositoryFactory: () -> AuthRepositoryContract,
) : AuthRepositoryContract {

  private val delegate: AuthRepositoryContract = if (authBaseUrl.isBlank()) {
    fallbackRepository
  } else {
    remoteRepositoryFactory()
  }

  override suspend fun requestOtp(email: String): AuthResult = delegate.requestOtp(email)

  override suspend fun verifyOtp(email: String, code: String): AuthResult = delegate.verifyOtp(email, code)

  override suspend fun logout(): AuthResult = delegate.logout()
}
