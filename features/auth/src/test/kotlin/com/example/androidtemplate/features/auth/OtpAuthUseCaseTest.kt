package com.example.androidtemplate.features.auth

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class OtpAuthUseCaseTest {

  @Test
  fun sendCode_invalidEmail_returnsErrorWithoutCallingRepository() = runBlocking {
    val fakeRepository = FakeAuthRepository()
    val useCase = OtpAuthUseCase(fakeRepository)

    val state = useCase.sendCode("invalid-email")

    assertThat(state).isInstanceOf(OtpFlowState.Error::class.java)
    assertThat(fakeRepository.sendOtpCallCount).isEqualTo(0)
  }

  @Test
  fun sendCode_success_transitionsToSentCodeState() = runBlocking {
    val fakeRepository = FakeAuthRepository(sendOtpResult = AuthResult.Success)
    val useCase = OtpAuthUseCase(fakeRepository)

    val state = useCase.sendCode("test@example.com")

    assertThat(state).isEqualTo(OtpFlowState.SentCode(email = "test@example.com", cooldownSeconds = 30))
    assertThat(fakeRepository.sendOtpCallCount).isEqualTo(1)
  }

  @Test
  fun sendCode_rateLimited_transitionsToRateLimitedState() = runBlocking {
    val fakeRepository = FakeAuthRepository(sendOtpResult = AuthResult.RateLimited(retryAfterSeconds = 42))
    val useCase = OtpAuthUseCase(fakeRepository)

    val state = useCase.sendCode("test@example.com")

    assertThat(state).isEqualTo(OtpFlowState.RateLimited(42))
  }

  @Test
  fun verifyCode_invalidCode_returnsErrorWithoutCallingRepository() = runBlocking {
    val fakeRepository = FakeAuthRepository()
    val useCase = OtpAuthUseCase(fakeRepository)

    val state = useCase.verifyCode(email = "test@example.com", code = "12")

    assertThat(state).isInstanceOf(OtpFlowState.Error::class.java)
    assertThat(fakeRepository.verifyOtpCallCount).isEqualTo(0)
  }

  @Test
  fun verifyCode_success_transitionsToVerifiedSuccess() = runBlocking {
    val fakeRepository = FakeAuthRepository(verifyOtpResult = AuthResult.Success)
    val useCase = OtpAuthUseCase(fakeRepository)

    useCase.sendCode("test@example.com")
    val state = useCase.verifyCode(email = "test@example.com", code = "123456")

    assertThat(state).isEqualTo(OtpFlowState.VerifiedSuccess)
    assertThat(fakeRepository.verifyOtpCallCount).isEqualTo(1)
  }

  private class FakeAuthRepository(
    private val sendOtpResult: AuthResult = AuthResult.Success,
    private val verifyOtpResult: AuthResult = AuthResult.Success,
  ) : AuthRepositoryContract {
    var sendOtpCallCount: Int = 0
    var verifyOtpCallCount: Int = 0

    override suspend fun requestOtp(email: String): AuthResult {
      sendOtpCallCount += 1
      return sendOtpResult
    }

    override suspend fun verifyOtp(email: String, code: String): AuthResult {
      verifyOtpCallCount += 1
      return verifyOtpResult
    }

    override suspend fun logout(): AuthResult = AuthResult.Success
  }
}
