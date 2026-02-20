package com.example.androidtemplate.features.auth

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ConfigurableAuthRepositoryTest {

  @Test
  fun blankBaseUrl_usesFallbackRepository() = runBlocking {
    val fallback = FakeRepository(AuthResult.Success)
    val remote = FakeRepository(AuthResult.Failure("remote should not be called"))

    val repository = ConfigurableAuthRepository(
      authBaseUrl = "   ",
      fallbackRepository = fallback,
      remoteRepositoryFactory = { remote },
    )

    val result = repository.requestOtp("user@example.com")

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(fallback.requestOtpCallCount).isEqualTo(1)
    assertThat(remote.requestOtpCallCount).isEqualTo(0)
  }

  @Test
  fun nonBlankBaseUrl_usesRemoteRepository() = runBlocking {
    val fallback = FakeRepository(AuthResult.Failure("fallback should not be called"))
    val remote = FakeRepository(AuthResult.Success)

    val repository = ConfigurableAuthRepository(
      authBaseUrl = "https://example.supabase.co",
      fallbackRepository = fallback,
      remoteRepositoryFactory = { remote },
    )

    val result = repository.verifyOtp("user@example.com", "123456")

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(remote.verifyOtpCallCount).isEqualTo(1)
    assertThat(fallback.verifyOtpCallCount).isEqualTo(0)
  }

  @Test
  fun clearLocalSession_delegatesToSelectedRepository() = runBlocking {
    val fallback = FakeRepository(AuthResult.Success)
    val remote = FakeRepository(AuthResult.Success)
    val repository = ConfigurableAuthRepository(
      authBaseUrl = "",
      fallbackRepository = fallback,
      remoteRepositoryFactory = { remote },
    )

    val result = repository.clearLocalSession()

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(fallback.clearLocalSessionCallCount).isEqualTo(1)
    assertThat(remote.clearLocalSessionCallCount).isEqualTo(0)
  }

  private class FakeRepository(
    private val result: AuthResult,
  ) : AuthRepositoryContract {
    var requestOtpCallCount: Int = 0
    var verifyOtpCallCount: Int = 0
    var clearLocalSessionCallCount: Int = 0

    override suspend fun requestOtp(email: String): AuthResult {
      requestOtpCallCount += 1
      return result
    }

    override suspend fun verifyOtp(email: String, code: String): AuthResult {
      verifyOtpCallCount += 1
      return result
    }

    override suspend fun logout(): AuthResult = result

    override suspend fun clearLocalSession(): AuthResult {
      clearLocalSessionCallCount += 1
      return result
    }
  }
}
