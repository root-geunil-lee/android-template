package com.example.androidtemplate.features.auth

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class OAuthUseCaseTest {

  @Test
  fun buildAuthorizeUrl_usesProviderAndRedirect() {
    val repo = FakeOAuthRepository(
      authorizeUrl = "https://supabase.example/auth/v1/authorize?provider=google",
    )
    val useCase = OAuthUseCase(repo)

    val result = useCase.start(OAuthProvider.Google)

    assertThat(result).isEqualTo(OAuthFlowState.LaunchBrowser("https://supabase.example/auth/v1/authorize?provider=google"))
    assertThat(repo.lastProvider).isEqualTo(OAuthProvider.Google)
  }

  @Test
  fun buildAuthorizeUrl_missingConfig_returnsError() {
    val repo = FakeOAuthRepository(authorizeUrl = null)
    val useCase = OAuthUseCase(repo)

    val result = useCase.start(OAuthProvider.Apple)

    assertThat(result).isEqualTo(OAuthFlowState.Error("Missing OAuth configuration"))
  }

  @Test
  fun handleCallback_success_returnsAuthenticated() = runBlocking {
    val repo = FakeOAuthRepository(callbackResult = AuthResult.Success)
    val useCase = OAuthUseCase(repo)

    val result = useCase.handleCallback("androidtemplate://auth/callback#access_token=token123")

    assertThat(result).isEqualTo(OAuthFlowState.Authenticated)
    assertThat(repo.lastCallbackUri).contains("access_token=token123")
  }

  @Test
  fun handleCallback_failure_returnsError() = runBlocking {
    val repo = FakeOAuthRepository(callbackResult = AuthResult.Failure("OAuth failed"))
    val useCase = OAuthUseCase(repo)

    val result = useCase.handleCallback("androidtemplate://auth/callback?error=access_denied")

    assertThat(result).isEqualTo(OAuthFlowState.Error("OAuth failed"))
  }

  private class FakeOAuthRepository(
    private val authorizeUrl: String? = null,
    private val callbackResult: AuthResult = AuthResult.Success,
  ) : AuthRepositoryContract {
    var lastProvider: OAuthProvider? = null
    var lastCallbackUri: String? = null

    override fun buildOAuthAuthorizeUrl(provider: OAuthProvider): String? {
      lastProvider = provider
      return authorizeUrl
    }

    override suspend fun completeOAuthCallback(callbackUri: String): AuthResult {
      lastCallbackUri = callbackUri
      return callbackResult
    }

    override suspend fun requestOtp(email: String): AuthResult = AuthResult.Success
    override suspend fun verifyOtp(email: String, code: String): AuthResult = AuthResult.Success
    override suspend fun logout(): AuthResult = AuthResult.Success
  }
}
